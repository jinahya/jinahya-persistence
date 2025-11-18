#!/bin/sh
./mvnw -P\
jakarta-ee-11.0.0,\
hibernate-orm-7.2-jakarta-ee-11,\
hibernate-validator-9.1-jakarta-ee-11,\
weld-6-jakarta-ee-11 \
"$@"
