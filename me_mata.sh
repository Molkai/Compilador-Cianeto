#!/bin/bash

for A in `cat kill-me.txt`
do
    `sed 'y/áÁàÀãÃâÂéÉêÊíÍóÓõÕôÔúÚçÇ/aAaAaAaAeEeEiIoOoOoOuUcC/' <Tests/$A> TestsBonitos/$A`
done
