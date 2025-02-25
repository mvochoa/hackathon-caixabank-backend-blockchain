package com.hackathon.blockchain.service;

import com.hackathon.blockchain.dto.transaction.HistoryTransactionDto;
import com.hackathon.blockchain.mapper.TransactionMapper;
import com.hackathon.blockchain.model.Asset;
import com.hackathon.blockchain.model.Transaction;
import com.hackathon.blockchain.model.User;
import com.hackathon.blockchain.model.Wallet;
import com.hackathon.blockchain.repository.TransactionRepository;
import com.hackathon.blockchain.repository.UserRepository;
import com.hackathon.blockchain.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MarketDataService marketDataService;
    private final BlockchainService blockchainService;

    public Optional<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    public Optional<Wallet> getWalletByAddress(String address) {
        return walletRepository.findByAddress(address);
    }

    @Transactional
    public void initializeLiquidityPools(Map<String, Double> initialAssets) {
        for (Map.Entry<String, Double> entry : initialAssets.entrySet()) {
            String symbol = entry.getKey();
            double initialQuantity = entry.getValue();

            String liquidityWalletAddress = "LP-" + symbol;
            Optional<Wallet> existingWallet = walletRepository.findByAddress(liquidityWalletAddress);

            if (existingWallet.isEmpty()) {
                Wallet liquidityWallet = new Wallet();
                liquidityWallet.setAddress(liquidityWalletAddress);
                liquidityWallet.setBalance(0.0);
                liquidityWallet.setAccountStatus("ACTIVE");

                Asset asset = new Asset(null, symbol, initialQuantity, 0.0, liquidityWallet);
                liquidityWallet.getAssets().add(asset);
                walletRepository.save(liquidityWallet);
            }
        }
    }

    /*
     * Los usuarios deben comprar primero USDT para poder cambiar por tokens
     * El dinero fiat no vale para comprar tokens
     * Cuando se intercambia USDT por cualquier moneda, no se añade USDT a los assets de otras monedas
     */
    @Transactional
    public String buyAsset(Long userId, String symbol, double quantity) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        Optional<Wallet> liquidityWalletOpt = walletRepository.findByAddress("LP-" + symbol);
        Optional<Wallet> usdtLiquidityWalletOpt = walletRepository.findByAddress("LP-USDT");

        if (optionalWallet.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Wallet not found!");
        if (liquidityWalletOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Liquidity pool for " + symbol + " not found!");
        if (usdtLiquidityWalletOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Liquidity pool for USDT not found!");

        Wallet userWallet = optionalWallet.get();
        Wallet liquidityWallet = liquidityWalletOpt.get();
        Wallet usdtLiquidityWallet = usdtLiquidityWalletOpt.get();

        double price = marketDataService.fetchLivePriceForAsset(symbol).doubleValue();
        double totalCost = quantity * price;

        if (symbol.equals("USDT")) {
            if (userWallet.getBalance() < totalCost) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Insufficient fiat balance to buy USDT!");
            }

            Transaction transactionUSDT = recordTransaction(usdtLiquidityWallet, userWallet, "USDT", quantity, price, "BUY");
            blockchainService.validateTransaction(transactionUSDT.getId(), symbol);

            userWallet.setBalance(userWallet.getBalance() - totalCost);
            updateWalletAssets(userWallet, "USDT", quantity);
            updateWalletAssets(usdtLiquidityWallet, "USDT", -quantity);

            walletRepository.save(userWallet);
            walletRepository.save(usdtLiquidityWallet);

            return "✅ USDT purchased successfully!";
        }

        Optional<Asset> usdtAssetOpt = userWallet.getAssets().stream()
                .filter(a -> a.getSymbol().equals("USDT"))
                .findFirst();

        if (usdtAssetOpt.isEmpty() || usdtAssetOpt.get().getQuantity() < totalCost) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Insufficient USDT balance! You must buy USDT first.");
        }

        Transaction transaction = recordTransaction(liquidityWallet, userWallet, symbol, quantity, price, "BUY");
        blockchainService.validateTransaction(transaction.getId(), symbol);

        updateWalletAssets(userWallet, "USDT", -totalCost);
        updateWalletAssets(usdtLiquidityWallet, "USDT", totalCost);

        updateWalletAssets(userWallet, symbol, quantity);
        updateWalletAssets(liquidityWallet, symbol, -quantity);

        walletRepository.save(userWallet);
        walletRepository.save(liquidityWallet);
        walletRepository.save(usdtLiquidityWallet);

        return "✅ Asset purchased successfully!";
    }

    /*
     * La venta siempre se hace por USDT
     * Los usuarios después pueden cambiar USDT por la moneda fiat
     */
    @Transactional
    public String sellAsset(Long userId, String symbol, double quantity) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        Optional<Wallet> liquidityWalletOpt = walletRepository.findByAddress("LP-" + symbol);

        if (optionalWallet.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Wallet not found!");
        if (liquidityWalletOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Liquidity pool for " + symbol + " not found!");

        Wallet userWallet = optionalWallet.get();
        Wallet liquidityWallet = liquidityWalletOpt.get();

        double price = marketDataService.fetchLivePriceForAsset(symbol).doubleValue();
        double totalRevenue = quantity * price;

        Optional<Asset> existingAsset = userWallet.getAssets().stream()
                .filter(a -> a.getSymbol().equals(symbol))
                .findFirst();

        if (existingAsset.isEmpty() || existingAsset.get().getQuantity() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Not enough assets to sell!");
        }

        // CASO 1: Venta de USDT (Recibo dinero fiat)
        if (symbol.equals("USDT")) {
            if (liquidityWallet.getAssets().stream().anyMatch(a -> a.getSymbol().equals("USDT") && a.getQuantity() < quantity)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Not enough USDT liquidity!");
            }

            Transaction transaction = recordTransaction(userWallet, liquidityWallet, symbol, quantity, price, "SELL");
            blockchainService.validateTransaction(transaction.getId(), symbol);

            userWallet.setBalance(userWallet.getBalance() + totalRevenue);
            updateWalletAssets(userWallet, symbol, -quantity);
            updateWalletAssets(liquidityWallet, symbol, quantity);

        } else {
            // CASO 2: Venta de otros assets (Recibo USDT)
            Optional<Wallet> usdtLiquidityWalletOpt = walletRepository.findByAddress("LP-USDT");
            if (usdtLiquidityWalletOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ USDT liquidity pool not found!");
            Wallet usdtLiquidityWallet = usdtLiquidityWalletOpt.get();

            Optional<Asset> usdtAssetOpt = usdtLiquidityWallet.getAssets().stream()
                    .filter(a -> a.getSymbol().equals("USDT"))
                    .findFirst();

            if (usdtAssetOpt.isEmpty() || usdtAssetOpt.get().getQuantity() < totalRevenue) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ Not enough USDT in liquidity pool!");
            }

            Transaction transaction = recordTransaction(userWallet, liquidityWallet, symbol, quantity, price, "SELL");
            blockchainService.validateTransaction(transaction.getId(), symbol);

            updateWalletAssets(userWallet, "USDT", totalRevenue);
            updateWalletAssets(userWallet, symbol, -quantity);
            updateWalletAssets(usdtLiquidityWallet, "USDT", -totalRevenue);
            updateWalletAssets(liquidityWallet, symbol, quantity);

            walletRepository.save(usdtLiquidityWallet);
        }

        walletRepository.save(userWallet);
        walletRepository.save(liquidityWallet);

        return "✅ Asset sold successfully!";
    }

    /*
     * Esta versión ya no almacena purchasePrice en Assets
     */
    private void updateWalletAssets(Wallet wallet, String assetSymbol, double amount) {
        Optional<Asset> assetOpt = wallet.getAssets().stream()
                .filter(asset -> asset.getSymbol().equalsIgnoreCase(assetSymbol))
                .findFirst();

        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            asset.setQuantity(asset.getQuantity() + amount);
            if (asset.getQuantity() <= 0) {
                wallet.getAssets().remove(asset);
            }
        } else if (amount > 0) {
            Asset newAsset = new Asset();
            newAsset.setSymbol(assetSymbol);
            newAsset.setQuantity(amount);
            newAsset.setWallet(wallet);
            wallet.getAssets().add(newAsset);
        }

        walletRepository.save(wallet);
    }

    private Transaction recordTransaction(Wallet sender, Wallet receiver, String assetSymbol, double quantity, double price, String type) {
        Transaction transaction = new Transaction(
                null,             // id (se genera automáticamente)
                sender,           // senderWallet
                receiver,         // receiverWallet
                assetSymbol,      // assetSymbol
                quantity,         // amount
                price,            // pricePerUnit
                type,             // type
                new Date(),       // timestamp
                "PENDING",        // status
                0.0,              // fee
                null              // block (aún no asignado)
        );

        return transactionRepository.save(transaction);
    }

    public String createWalletForUser(User user) {
        Optional<Wallet> existingWallet = walletRepository.findByUserId(user.getId());
        if (existingWallet.isPresent()) {
            return "❌ You already have a wallet created.";
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setAddress(generateWalletAddress());
        wallet.setBalance(100000.0);
        wallet.setAccountStatus("ACTIVE");

        walletRepository.save(wallet);

        return "✅ Wallet successfully created! Address: " + wallet.getAddress();
    }

    private String generateWalletAddress() {
        return DigestUtils.sha256Hex(UUID.randomUUID().toString());
    }


    // Ejecuto esta función para tener patrimonios de carteras actualizados continuamente y que no contenga valores estáticos
    @Scheduled(fixedRate = 30000) // Se ejecuta cada 30 segundos
    @Transactional
    public void updateWalletBalancesScheduled() {
        log.info("🔄 Updating wallet net worths based on live market prices...");

        for (Wallet wallet : walletRepository.findAll()) {
            double totalValue = 0.0;

            for (Asset asset : wallet.getAssets()) {
                double marketPrice = marketDataService.fetchLivePriceForAsset(asset.getSymbol()).doubleValue();
                double assetValue = asset.getQuantity() * marketPrice;
                totalValue += assetValue;

                log.info("💰 Asset {} - Quantity: {} - Market Price: {} - Total Value: {}",
                        asset.getSymbol(), asset.getQuantity(), marketPrice, assetValue);
            }

            if (wallet.getUser() != null) {
                totalValue += wallet.getBalance();
            }

            double previousNetWorth = wallet.getNetWorth();
            wallet.setNetWorth(totalValue);
            walletRepository.save(wallet);

            log.info("📊 Wallet [{}] - Previous Net Worth: {} - Updated Net Worth: {}",
                    wallet.getAddress(), previousNetWorth, totalValue);

            Wallet savedWallet = walletRepository.findById(wallet.getId()).orElse(null);
            if (savedWallet != null) {
                log.info("✅ Confirmed DB Update - Wallet [{}] New Net Worth: {}", savedWallet.getAddress(), savedWallet.getNetWorth());
            } else {
                log.error("❌ Failed to fetch wallet [{}] after update!", wallet.getAddress());
            }
        }

        log.info("✅ All wallet net worths updated successfully!");
    }

    public Map<String, Object> getWalletBalance(Long userId) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        if (optionalWallet.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
        }

        Wallet wallet = optionalWallet.get();
        Map<String, BigDecimal> assetPrices = marketDataService.fetchLiveMarketPrices();

        Map<String, Double> assetsMap = new HashMap<>();
        double netWorth = wallet.getBalance();

        for (Asset asset : wallet.getAssets()) {
            double currentPrice = assetPrices.getOrDefault(asset.getSymbol(), BigDecimal.valueOf(0.0)).doubleValue();
            double assetValue = asset.getQuantity() * currentPrice;
            assetsMap.put(asset.getSymbol(), assetValue);
            netWorth += assetValue;
        }

        Map<String, Object> walletInfo = new HashMap<>();
        walletInfo.put("wallet_address", wallet.getAddress());
        walletInfo.put("cash_balance", wallet.getBalance());
        walletInfo.put("net_worth", netWorth);
        walletInfo.put("assets", assetsMap);

        return walletInfo;
    }

    /**
     * Devuelve un mapa con dos listas de transacciones:
     * - "sent": transacciones enviadas (donde la wallet es remitente)
     * - "received": transacciones recibidas (donde la wallet es destinataria)
     */
    public HistoryTransactionDto getWalletTransactions(Long walletId) {
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
        }
        Wallet wallet = walletOpt.get();

        List<Transaction> sentTransactions = transactionRepository.findBySenderWallet(wallet);
        List<Transaction> receivedTransactions = transactionRepository.findByReceiverWallet(wallet);

        return HistoryTransactionDto.builder()
                .sent(sentTransactions.stream().map(TransactionMapper::entityToDto).toList())
                .received(receivedTransactions.stream().map(TransactionMapper::entityToDto).toList())
                .build();
    }

    public HistoryTransactionDto getWalletTransactionsByUer(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return getWalletTransactions(userOpt.get().getWallet().getId());
    }
}