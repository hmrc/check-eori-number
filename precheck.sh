#!/bin/bash

sbt clean scalafmtAll test:scalafmtAll coverage test it/test coverageReport