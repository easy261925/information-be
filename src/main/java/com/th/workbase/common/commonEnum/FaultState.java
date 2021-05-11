package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 故障状态枚举类
 * @Version 1.0
 */
public enum FaultState {
    Create("0"),  //故障发生
    Ignore("1"),    //忽略故障
    Processing("2"),//故障处理中
    End("3")    //故障结束
    ;
    private final String state;

    FaultState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static FaultState getFaultState(String state) {
        for (FaultState taskState : values()) {
            if (taskState.getState().equals(state)) {
                return taskState;
            }
        }
        return null;
    }
}

