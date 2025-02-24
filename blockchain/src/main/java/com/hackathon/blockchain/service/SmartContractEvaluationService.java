package com.hackathon.blockchain.service;

import com.hackathon.blockchain.dto.CreateSmartContractDto;
import com.hackathon.blockchain.model.SmartContract;
import com.hackathon.blockchain.model.Transaction;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.model.WalletKey;
import com.hackathon.blockchain.repository.SmartContractRepository;
import com.hackathon.blockchain.repository.TransactionRepository;
import com.hackathon.blockchain.repository.WalletKeyRepository;
import com.hackathon.blockchain.repository.WalletRepository;
import com.hackathon.blockchain.utils.SignatureUtil;
import lombok.AllArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SmartContractEvaluationService {

    private final WalletKeyRepository walletKeyRepository;
    private final SmartContractRepository smartContractRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletKeyService walletKeyService;
    private final SpelExpressionParser parser;

    public SmartContract create(CreateSmartContractDto contract) {
        SmartContract smartContract = SmartContract.builder()
                .issuerWalletId(contract.getIssuerWalletId())
                .name(contract.getName())
                .conditionExpression(contract.getConditionExpression())
                .action(contract.getAction())
                .actionValue(contract.getActionValue())
                .issuerWalletId(contract.getIssuerWalletId())
                .build();

        Optional<Wallet> optionalWallet = walletRepository.findById(smartContract.getIssuerWalletId());
        if (optionalWallet.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Wallet not found!");

        Optional<WalletKey> optionalWalletKey = walletKeyRepository.findByWalletId(optionalWallet.get().getId());
        if (optionalWalletKey.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Wallet Key not found!");

        WalletKey walletKey = optionalWalletKey.get();
        PrivateKey privateKey = walletKeyService.getPrivateKeyForWallet(walletKey.getWallet().getId());
        String dataToSign = contract.getName() +
                contract.getConditionExpression() +
                contract.getAction() +
                contract.getActionValue() +
                contract.getIssuerWalletId();

        try {
            smartContract.setDigitalSignature(SignatureUtil.signature(dataToSign, privateKey));
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ The smart contract could not be signed!");
        }

        return smartContractRepository.save(smartContract);
    }

    /**
     * Verifica la firma digital del contrato usando la clave pública del emisor.
     */
    public boolean verifyContractSignature(SmartContract contract) {
        try {
            PublicKey issuerPublicKey = walletKeyService.getPublicKeyForWallet(contract.getIssuerWalletId());
            if (issuerPublicKey == null) {
                return false;
            }
            String dataToSign = contract.getName() +
                    contract.getConditionExpression() +
                    contract.getAction() +
                    contract.getActionValue() +
                    contract.getIssuerWalletId();
            return SignatureUtil.verifySignature(dataToSign, contract.getDigitalSignature(), issuerPublicKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String verifyContractSignature(Long smartContractId) {
        Optional<SmartContract> smartContractOpt = smartContractRepository.findById(smartContractId);
        if (smartContractOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "❌ The smart contract not found!");

        if (verifyContractSignature(smartContractOpt.get())) {
            return "Smart contract is valid";
        }

        return "Smart contract is invalid";
    }

    /**
     * Evalúa todos los smart contracts activos sobre las transacciones pendientes.
     * Se inyectan las variables "amount" y "txType" en el contexto de SpEL.
     * Si la condición se cumple y la firma es válida, se ejecuta la acción definida:
     * - Para "CANCEL_TRANSACTION", se marca la transacción como "CANCELED".
     * - (Si hubiera otras acciones, se podrían implementar aquí).
     */
    @Transactional
    public void evaluateSmartContracts() {
        List<SmartContract> contracts = (List<SmartContract>) smartContractRepository.findAll(); // O filtrar por "ACTIVE"
        List<Transaction> pendingTxs = transactionRepository.findByStatus("PENDING");

        for (Transaction tx : pendingTxs) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("amount", tx.getAmount());
            context.setVariable("txType", tx.getType());
            for (SmartContract contract : contracts) {
                if (!verifyContractSignature(contract)) continue;
                Expression exp = parser.parseExpression(contract.getConditionExpression());
                Boolean conditionMet = exp.getValue(context, Boolean.class);
                if (conditionMet != null && conditionMet) {
                    if ("CANCEL_TRANSACTION".equalsIgnoreCase(contract.getAction())) {
                        tx.setStatus("CANCELED");
                    } else if ("TRANSFER_FEE".equalsIgnoreCase(contract.getAction())) {
                        transferFee(tx, contract.getActionValue());
                        tx.setStatus("PROCESSED_CONTRACT");
                    }
                    transactionRepository.save(tx);
                }
            }
        }
    }

    // Método para transferir el fee: deducirlo del wallet del emisor y sumarlo a la wallet de fees.
    public void transferFee(Transaction tx, double fee) {
        Wallet sender = tx.getSenderWallet();
        // Supongamos que el liquidity pool de USDT (o la wallet designada para fees) tiene ID 2.
        Optional<Wallet> feeWalletOpt = walletRepository.findByAddress("FEES-USDT");
        if (feeWalletOpt.isPresent()) {
            Wallet feeWallet = feeWalletOpt.get();
            // Actualiza los balances:
            sender.setBalance(sender.getBalance() - fee);
            feeWallet.setBalance(feeWallet.getBalance() + fee);
            walletRepository.save(sender);
            walletRepository.save(feeWallet);
        }
    }

    // Método para crear una wallet para fees (solo USDT)
    public String createFeeWallet() {
        String feeWalletAddress = "FEES-USDT";
        Optional<Wallet> existing = walletRepository.findByAddress(feeWalletAddress);
        if (existing.isPresent()) {
            return "Fee wallet already exists with address: " + feeWalletAddress;
        }
        Wallet feeWallet = new Wallet();
        feeWallet.setAddress(feeWalletAddress);
        feeWallet.setBalance(0.0);
        feeWallet.setAccountStatus("ACTIVE");
        // Al no estar asociada a un usuario, se deja user en null
        walletRepository.save(feeWallet);
        return "Fee wallet created successfully with address: " + feeWalletAddress;
    }


    // UNA UNICA CONDICION
    // /**
    //  * Evalúa todos los smart contracts activos sobre las transacciones pendientes.
    //  * Para cada transacción con estado "PENDING", se evalúa la expresión condicional del contrato.
    //  * Si se cumple y la firma es válida, se ejecuta la acción definida (por ejemplo, transferir fee)
    //  * y se actualiza el estado de la transacción a "PROCESSED_CONTRACT".
    //  */
    // @Transactional
    // public void evaluateSmartContracts() {
    //     List<SmartContract> contracts = smartContractRepository.findByStatus("ACTIVE");
    //     // Obtén todas las transacciones pendientes.
    //     List<Transaction> pendingTxs = transactionRepository.findByStatus("PENDING");

    //     for (Transaction tx : pendingTxs) {
    //         // Creamos un contexto de evaluación y definimos variables que se puedan usar en la expresión.
    //         StandardEvaluationContext context = new StandardEvaluationContext();
    //         context.setVariable("amount", tx.getAmount());
    //         // Puedes inyectar otras variables según convenga.

    //         for (SmartContract contract : contracts) {
    //             // Primero, verificar la firma del contrato.
    //             if (!verifyContractSignature(contract)) {
    //                 // Si la firma no es válida, se ignora este contrato.
    //                 continue;
    //             }

    //             // Evaluar la condición del contrato usando SpEL.
    //             Expression exp = parser.parseExpression(contract.getConditionExpression());
    //             Boolean conditionMet = exp.getValue(context, Boolean.class);

    //             if (conditionMet != null && conditionMet) {
    //                 // Si la condición se cumple y la acción es "TRANSFER_FEE", se ejecuta la transferencia.
    //                 if ("TRANSFER_FEE".equalsIgnoreCase(contract.getAction())) {
    //                     walletService.transferFee(tx, contract.getActionValue());
    //                     tx.setStatus("PROCESSED_CONTRACT");
    //                     transactionRepository.save(tx);
    //                 }
    //                 // Aquí se pueden agregar más acciones según el contrato.
    //             }
    //         }
    //     }
    // }
}