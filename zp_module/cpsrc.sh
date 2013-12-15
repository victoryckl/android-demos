#!/bin/bash

if [ $# -eq 0 ]; then
  echo copy to aosp
  cp -r /mnt/hgfs/E/myprj/zp_module ../;
else
  if [ $1 = "toxp" ]; then
    echo copy to xp
    cp -r ../zp_module /mnt/hgfs/E/myprj/;
  fi
fi
