#!/bin/bash

gradle jar

java -jar -Xmx32g build/libs/experiment-0.1.jar