#1、TCP下行报文数 (语句行里不可有#符号)
SELECT   sum(DlIpPacketNum)  from s1_u where L4Protocol=0 