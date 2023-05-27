package com.zjs.mj.util;


import com.zjs.mj.enums.Action;
import lombok.Data;

@Data
public class MessageData {
	private Action action;
	private String prompt;
	private int index;
	private String status;
}
