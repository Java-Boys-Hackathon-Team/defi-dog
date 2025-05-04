package ru.javaboys.defidog.integrations.blockchain;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainServiceImpl implements BlockchainService {

    private final Web3j web3j;

    @Override
    @SneakyThrows
    public BigInteger getLastBlockNumber() {

        EthBlockNumber latestBlock = web3j.ethBlockNumber().send();

        return latestBlock.getBlockNumber();
    }
}
