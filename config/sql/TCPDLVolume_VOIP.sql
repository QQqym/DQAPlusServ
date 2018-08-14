#2、TCP下行流量
SELECT SUM(DlDataLen) from voip  WHERE  L4Protocol=0
