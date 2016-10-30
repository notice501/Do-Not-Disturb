package foocoder.dnd.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

public class PinyinUtil {

    private static List<String> multipleName;

    private static List<String> multiplePinyin;

    static {
        multipleName = new ArrayList<>();
        multiplePinyin = new ArrayList<>();
        multipleName.add("秘");
        multiplePinyin.add("bi");
        multipleName.add("卜");
        multiplePinyin.add("bu");
        multipleName.add("长");
        multiplePinyin.add("chang");
        multipleName.add("种");
        multiplePinyin.add("chong");
        multipleName.add("重");
        multiplePinyin.add("chong");
        multipleName.add("刀");
        multiplePinyin.add("diao");
        multipleName.add("干");
        multiplePinyin.add("gan");
        multipleName.add("葛");
        multiplePinyin.add("ge");
        multipleName.add("盖");
        multiplePinyin.add("ge");
        multipleName.add("过");
        multiplePinyin.add("guo");
        multipleName.add("华");
        multiplePinyin.add("hua");
        multipleName.add("纪");
        multiplePinyin.add("ji");
        multipleName.add("筠");
        multiplePinyin.add("jun");
        multipleName.add("牟");
        multiplePinyin.add("mu");
        multipleName.add("区");
        multiplePinyin.add("ou");
        multipleName.add("繁");
        multiplePinyin.add("po");
        multipleName.add("仇");
        multiplePinyin.add("qiu");
        multipleName.add("任");
        multiplePinyin.add("ren");
        multipleName.add("单");
        multiplePinyin.add("shan");
        multipleName.add("召");
        multiplePinyin.add("shao");
        multipleName.add("折");
        multiplePinyin.add("she");
        multipleName.add("舍");
        multiplePinyin.add("she");
        multipleName.add("沈");
        multiplePinyin.add("shen");
        multipleName.add("峙");
        multiplePinyin.add("shi");
        multipleName.add("隗");
        multiplePinyin.add("wei");
        multipleName.add("解");
        multiplePinyin.add("xie");
        multipleName.add("莘");
        multiplePinyin.add("xin");
        multipleName.add("燕");
        multiplePinyin.add("yan");
        multipleName.add("尉");
        multiplePinyin.add("yu");
        multipleName.add("乐");
        multiplePinyin.add("yue");
        multipleName.add("员");
        multiplePinyin.add("yun");
        multipleName.add("查");
        multiplePinyin.add("zha");
        multipleName.add("翟");
        multiplePinyin.add("zhai");
        multipleName.add("曾");
        multiplePinyin.add("zeng");

    }

    public static String getPinYin(String zhongwen)
            throws BadHanyuPinyinOutputFormatCombination {

        String zhongWenPinYin = "";
        char[] chars = zhongwen.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                if (multipleName.contains(String.valueOf(chars[i]))) {
                    zhongWenPinYin += multiplePinyin.get(multipleName.indexOf(String.valueOf(chars[i])));
                    continue;
                }
            }
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i], getDefaultOutputFormat());

            if (pinYin != null) {
                zhongWenPinYin += pinYin[0];
            } else {
                zhongWenPinYin += chars[i];
            }
        }
        return zhongWenPinYin.toLowerCase();
    }

    private static HanyuPinyinOutputFormat getDefaultOutputFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
        return format;
    }

    public static String getPinYinHeadChar(String str)
            throws BadHanyuPinyinOutputFormatCombination {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);

            if (j == 0) {
                if (multipleName.contains(String.valueOf(word))) {
                    convert += multiplePinyin.get(multipleName.indexOf(String.valueOf(word))).charAt(0);
                    continue;
                }
            }

            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word, getDefaultOutputFormat());
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toLowerCase();
    }

    public static String getPinYinInitChar(String str) {
        String init = "";
        try {
            init = str.substring(0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return init;
    }
}


