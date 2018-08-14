#2、TCP下行流量
SELECT SUM(DlDataLen) from s1_u  WHERE  L4Protocol=0
