package eci.cosw.data.model;

import java.util.Date;

public class Todo {
	private String description;
	private int priority;
	private Date dueDate;
	private String responsible;
	private String status;
	
	public Todo(String description, int priority, Date dueDate, String responsible, String status) {
		this.description = description;
		this.priority = priority;
		this.dueDate = dueDate;
		this.responsible = responsible;
		this.status = status;
	}
	
	public Todo(){
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Todo[description=" + description + ", priority=" + priority + ", dueDate=" + dueDate + ", responsible="
				+ responsible + ", status=" + status + "]";
	}
}
