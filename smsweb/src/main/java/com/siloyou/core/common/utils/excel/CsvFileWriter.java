package com.siloyou.core.common.utils.excel;

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.OutputStreamWriter;  
import java.util.List;  
import com.opencsv.CSVWriter;
import com.siloyou.core.common.config.Global;  
  
public abstract class CsvFileWriter<T> {  
  
	protected abstract  String[] getCsvTitle();
    protected abstract List<String[]> getCsvContent(int pageNum, int pageSize);  
      
    protected abstract String getCsvFileName();  
      
    protected abstract String getUuid();  
  
    public String execute() throws Exception {  
    	return execute(10000);
    }
    
    public String execute(int pageSize) throws Exception {  
        String result = "";  
        String savePath = Global.getConfig("smsTask.phoneList.dir") + File.separator;
          
        //如果保存路径不存在，则自动创建  
        File file = new File(savePath);  
        if (!file.exists()) {  
            file.mkdir();  
        }  
        String filePath = savePath + getUuid() + getCsvFileName();  
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath, true), "GBK");  
        CSVWriter writer = new CSVWriter(out);  
        writer.writeNext(getCsvTitle());
        
        int pageNo = 0;
        int index = 0;
        while(true) {
            List<String[]> csvContent = getCsvContent(pageNo, pageSize);
            writer.writeAll(csvContent);
            if(csvContent.size() < pageSize){
            	break;
            }
        	index ++;
            pageNo = index * pageSize;
        }
        writer.close();  
        result = filePath;  
  
        return result;
    }  
}