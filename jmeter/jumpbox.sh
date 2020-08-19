#!/bin/bash

# Helper script for running JMeter tests from a jumpbox

# Assume jumpbox instance doesn't have much memory
export HEAP="-Xmx512m"
# duration: number of seconds to run the test for
# host: DNS name of NHAIS adaptor LB
# See nahis.jmx for other options, but defaults should be fine
JOPTS="-Jduration=10 -Jhost=$1"
echo "Removing old reports/"
rm -Rf reports/
echo "Removing old results.csv"
rm -f results.csv
~/apache-jmeter-5.3/bin/jmeter -n -t nhais.jmx -l results.csv -e -o reports/ ${JOPTS}
