FROM mythril/myth:latest

WORKDIR /repo

ENTRYPOINT ["myth", "analyze"]