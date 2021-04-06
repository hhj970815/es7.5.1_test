package cn.es.search.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author huanghuajie
 * @version 1.0
 * @date 2021/3/31 17:19
 */

@Data
public class WareDetailBo {

    private Integer id;

    private String warename;

    private String intro;

    private String commonName;

    private String alias;

    private String medType;

    private String medSort;

    private String medForm;

    private String isChu;

    private String isYibao;

    private String isJiyao;

    private String medWay;

    private String medSick;

    private List<String> wareUrlList;

    private List<String> availTagsList;

    private String suitSymptom;

    private String forbidden;

    private String badFact;

    private String mainFunc;

    private String usageNum;

    private String attention;

}
