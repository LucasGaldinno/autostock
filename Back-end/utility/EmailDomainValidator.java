package br.com.AutoStock.utility;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class EmailDomainValidator {
    public static boolean isDomainValid(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);
            Record[] records = new Lookup(domain, Type.MX).run();
            if (records == null) return false;
            for (Record record : records) {
                if (record instanceof MXRecord) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
