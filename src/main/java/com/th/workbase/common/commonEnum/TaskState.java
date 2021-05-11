package com.th.workbase.common.commonEnum;

/**
 * @Date 2021-03-02-11:00
 * @Author tangJ
 * @Description 任务状态枚举类
 * @Version 1.0
 */
public enum TaskState {
    Create("0"),  //任务创建,电铲触发
    Publish("10"), //任务发布,电铲上线触发,发出两条任务,电铲将此任务告知大车
    Receive("20"), //任务接收,大车触发,大车接收任务后,将大车设备编码绑定到任务接收者
    ArriveShovel("21"),//大车到达电铲
    LoadComplete("30"),  //装车完成, 电铲触发
    LeaveShovel("40"),  //装车完成,离开电铲,大车触发,电铲发出下一个任务
    ArriveField("50"),//到达场地,大车触发
    Unload("60"),  //卸车,矿破触发
    LeaveField("70"),   //卸车完成,离开矿破,大车触发
    TMP("80"),   //临时状态,用于大车装岩石时,临时保存之前的任务,在大车离开电铲的时候先将此状态恢复为创建
    ToDoTaskCreate("100"),//待办任务创建
    ToDoTaskProcessing("110"),//待办任务被接受,处理中
    ToDoTaskCompleted("120"),//待办任务完成

    ;
    private String state;

    TaskState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static TaskState getTaskState(String state) {
        for (TaskState taskState : values()) {
            if (taskState.getState().equals(state)) {
                return taskState;
            }
        }
        return null;
    }
}

