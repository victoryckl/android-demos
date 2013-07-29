#!/system/bin/sh

COUNTER=0
while [ $COUNTER -lt 100 ]
do
  COUNTER=`expr $COUNTER + 1`
  echo $COUNTER
  monkey -p org.ckl.nativetimer -v 50
done

