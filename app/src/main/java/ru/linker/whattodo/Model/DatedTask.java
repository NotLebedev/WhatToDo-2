package ru.linker.whattodo.Model;

import java.util.Calendar;

/**
 * Created by root on 3/23/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public class DatedTask extends Task {

    private Calendar dateEnd;
    private Calendar dateStart;
    private Long dTotal;

    public DatedTask(String taskDescription, Integer priority, Calendar dateEnd) throws DateBeforeTodayException {
        super(taskDescription, priority);

        dateStart = Calendar.getInstance();

        if (dateEnd.compareTo(dateStart) < 0)
            throw new DateBeforeTodayException();

        this.dateEnd = dateEnd;

        dTotal = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();

    }

    public DatedTask(String taskDescription, Integer priority, Calendar dateEnd, Calendar dateStart) throws DateBeforeTodayException {
        super(taskDescription, priority);

        this.dateStart = dateStart;

        if (dateEnd.compareTo(dateStart) < 0)
            throw new DateBeforeTodayException();

        this.dateEnd = dateEnd;
        System.out.println("Date start : " + dateStart.getTimeInMillis() + ";\n Date end : " + dateEnd.getTimeInMillis());

        dTotal = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();

    }

    public Calendar getDateStart() {
        return dateStart;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DatedTask)
            return super.equals(obj) && (((DatedTask) obj).getDateStart() == this.dateStart) && (((DatedTask) obj).getDateEnd() == this.dateEnd);
        else
            return super.equals(obj);

    }

    public Calendar getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Calendar dateEnd) {
        this.dateEnd = dateEnd;
        dTotal = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();
    }

    @Override
    public Integer getPriority() {

        Long dStart = Calendar.getInstance().getTimeInMillis() - dateStart.getTimeInMillis();

        if (dStart > dTotal)
            return super.getPriority();

        System.out.println("Dated tsk with priority : " + (Long.valueOf(super.getPriority() * dStart / dTotal)).intValue());

        return (Long.valueOf(super.getPriority() * dStart / dTotal)).intValue();

    }
}
