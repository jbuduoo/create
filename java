//因為sql陣列中會有數筆insert，而他們的主鍵相同(keyno)。故先將主鍵設為aaa。 先找出相同的案件，再將keyno值放置新的。重複的insert則改為update
package com.jbuduoo;


import java.util.Hashtable;
import java.util.Vector;


public class keyNo01 {
    /*
     * 因為sql陣列中會有數筆insert，而他們的主鍵相同(keyno)。故先將主鍵設為aaa。
     * 先找出相同的案件，再將keyno值放置新的。重複的insert則改為update
     * 
     */


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str = "delete from wptlmeman where meid='FM0940009'@insert into wptlmeman (meid,memanid) values ('FM0940009','MA0020')@insert into wptlmeman (meid,memanid) values ('FM0940009','MA0022')@insert into wptlapre (lapkey,id) values ('63966','22338')@"
                + " insert into wptlproexte (meid,pamanid,desctext,goodname1,markname,marktype,markmeid,idkey,rcvid,keyno) values ('FM0940009','','0','','1','0','0','PC0000249242','3041002017002','aaa')@"
                + "insert into wptlproexte (meid,pamanid,desctext,goodname1,markname,marktype,markmeid,idkey,rcvid,keyno) values ('FM0940010','','0','','1','0','0','PC0000249243','3041002017002','aaa')@"
                + "insert into wptlproexte (meid,pamanid,desctext,goodname1,markname,marktype,markmeid,idkey,rcvid,keyno) values ('FM0940010','1','1','1','1','0','0','PC0000249243','3041002017002','aaa')";
        // 全部轉為小寫，方便比較
        str = str.toLowerCase();
        String[] strArray = str.split("@");
        insertAddKeyno(strArray);
    }


    /*
     * 將字串轉陣列 找出insert的資料，並加入keyno 重覆的keyno，則呼叫副表式，將資料改成updata，及之前那筆資料的key值。
     */
    public static String[] insertAddKeyno(String[] strArray) {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        String keyno = "";// keyno的值


        for (int i = 0; i < strArray.length; i++) {
            if ((strArray[i].indexOf("aaa")) >= 0) {
                int pcNum = strArray[i].indexOf("pc"); // 尋找idkey
                String pcStr = strArray[i].substring(pcNum, pcNum + 12);// 取得IDKEY
//                System.out.println(pcStr);


                if (ht.get(pcStr) != null) {
                    // 取出上一筆的keyno值
                    keyno = (String) ht.get(pcStr);
                    //若兩筆pc相同，則一筆改為update，並將keyno一致
                    String sql = insertToUpdate(keyno, strArray[i]);
                    System.out.println(sql);
                    continue;
                }
                keyno = "adalove" + i;// 取得keyno getKeyno();
                strArray[i] = strArray[i].replace("aaa", keyno);
//                System.out.println(strArray[i]);
                // 放入(idkey,keyno)
                ht.put(pcStr, keyno);
            }
            System.out.println(strArray[i].trim());
        }
        return strArray;


    }
    /* 將重覆的insert轉換成update
     * 
     */
    public static String insertToUpdate(String keyno, String str) {
        String[] strtemp = str.split(" ");
        String tableName = strtemp[2];
        String where = " where keyno='" + keyno + "'";
        int brackets = str.indexOf("(");
        int brackets2 = str.indexOf(")");
        String str2 = str.substring(brackets + 1, brackets2);// 取前面括號內的內容
//        System.out.println(str2);
        String str3 = str.substring(brackets2 + 1); // 取那面那段
//        System.out.println(str3);    
        int brackets3 = str3.indexOf("(");
        int brackets4 = str3.indexOf(")");
        String str4 = str3.substring(brackets3 + 1, brackets4);// 取後面括號內的內容
//        System.out.println(str4);


        String[] fieldname = str2.split(",");
        String[] fieldvalue = str4.split(",");


        String sql = "update " + tableName + " set ";
        StringBuffer sa = new StringBuffer();
        for (int i = 0; i < fieldname.length; i++) {
            if (fieldname[i].equals("keyno")) {
                continue;
            }
            sa.append(fieldname[i] + "=" + fieldvalue[i] + ",");
        }
        sa.setLength(sa.length() - 1);
        sql = sql + sa.toString() + where;


        return sql;
    }
}
