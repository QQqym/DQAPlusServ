#1、TCP下行报文数 (语句行里不可有#符号)
SELECT   sum(DlIpPacketNum)  from IM where L4Protocol=0 