#!/bin/bash
cd "$(dirname "$0")"
exec java -Djava.net.preferIPv4Stack=true -cp "target/classes:$(cat cp.txt)" com.example.loanbot.Main
