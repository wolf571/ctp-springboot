package ctp.thostmduserapi.test;

import ctp.thosttraderapi.utils.DateHelper;
import ctp.thosttraderapi.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalTime;
import java.util.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CtpTest {

    @Test
    public void testIn3Seconds() {
        String time = "11:00:39";

        long diffs = DateHelper.getDiffSeconds(LocalTime.parse(time));
        boolean in3 = diffs < 3000 && diffs > -3000;
        System.out.println(in3);
    }

    private Map<String, List<String>> contractsMap = new HashMap<>();

    @Test
    public void testMap() {
        String[] strs = {"{\"productID\": \"fu\",\"instrumentID\":\"fu2001\"}", "{\"productID\": \"fu\",\"instrumentID\":\"fu2005\"}"};
        Arrays.asList(strs).forEach(s -> {
            Map map = JsonHelper.toMap(s);
            String product = map.get("productID").toString();
            //合约代码
            String contract = map.get("instrumentID").toString();
            List<String> ins = contractsMap.get(product);
            if (ins == null) {
                ins = new ArrayList<>();
                contractsMap.put(product, ins);
            }
            if (!ins.contains(contract)) {
                ins.add(contract);
            }
        });

        contractsMap.forEach((k, v) -> {
            String[] ins = new String[v.size()];
            v.toArray(ins);
            System.out.println(ins);
        });
    }
}
