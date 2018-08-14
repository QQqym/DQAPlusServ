# MME成功建立的缺省S5S8承载数
SELECT count(*) from s10 where ProcedureType=1 and ProcedureStatus=0 
	AND (Bearer1Type=1 or Bearer2Type=1 or Bearer3Type=1  or Bearer4Type=1
		or Bearer5Type=1  or Bearer6Type=1  or Bearer7Type=1  or Bearer8Type=1 
		or Bearer9Type=1  or Bearer10Type=1  or Bearer11Type=1  or Bearer12Type=1
		or Bearer13Type=1  or Bearer14Type=1  or Bearer15Type=1 or Bearer16Type=1)
