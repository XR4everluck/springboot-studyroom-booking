package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "座位&标签关联表")
@TableName(value = "seat_label")
@Data
@Builder
public class SeatLabel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "座位id")
    @TableField(value = "seat_id")
    private Long seatId;

    @Schema(description = "标签id")
    @TableField(value = "label_id")
    private Long labelId;

}