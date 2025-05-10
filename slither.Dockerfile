FROM ghcr.io/crytic/slither:latest

WORKDIR /repo

ENTRYPOINT ["slither"]