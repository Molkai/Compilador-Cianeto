#!/bin/bash

for A in `cat kill-me.txt`
do
    `sed 'y/áÁàÀãÃâÂéÉêÊíÍóÓõÕôÔúÚçÇ/aAaAaAaAeEeEiIoOoOoOuUcC/' <Testes-12-01/$A> TestsBonitos/$A`
done
