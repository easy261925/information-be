package com.th.workbase.bean.plan;

import lombok.Data;

/**
 * @Date 2021-03-05-14:20
 * @Author tangJ
 * @Description 任务的视图类
 * @Version 1.0
 */
@Data
public class PlanTaskVo extends PlanTaskDto{
    private String taskText;
    private String createDateTime;
    private String destinationName;
    private String shovelName;
    private String categoryName;
}
