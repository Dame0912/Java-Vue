package com.dame.cn.beans.vo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeptListResult {

    private String companyName = "迪米信息科技有限公司";
    private String companyManage = "迪米"; //公司联系人
    private List<DeptWithChildItem> deptCategory;

    public DeptListResult(List deptCategory) {
        this.deptCategory = deptCategory;
    }
}
