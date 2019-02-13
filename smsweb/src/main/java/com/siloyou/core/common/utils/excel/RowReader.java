package com.siloyou.core.common.utils.excel;

import java.util.List;

public class RowReader implements IRowReader{  
	  
	  
    /* 业务逻辑实现方法 
     * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List) 
     */  
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {  
        // TODO Auto-generated method stub  
        //System.out.print(curRow+" "); 
    	System.out.print(rowlist.size()+" "); 
        for (int i = 0; i < rowlist.size(); i++) {  
            System.out.print(rowlist.get(i) + " ");  
        }  
        System.out.println();  
    }  
  
    public static void main(String[] args) throws Exception {  
        IRowReader reader = new RowReader();  
        //ExcelReaderUtil.readExcel(reader, "F://te03.xls");  
        ExcelReaderUtil.readExcel(reader, "D://daoru3.xls");
    }

	@Override
	public String getResult() {
		return null;
	}  
}  
