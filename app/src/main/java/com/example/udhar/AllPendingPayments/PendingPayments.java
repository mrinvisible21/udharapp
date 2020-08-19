package com.example.udhar.AllPendingPayments;


public class PendingPayments {

   private  String id,u_id,m_id,m_name,remaining_amount,end_date;

    public PendingPayments(String m_name) {
        this.m_name= m_name;
    }

    public String getId() { return id; }
    public String getU_id() { return u_id; }
    public String getM_id() { return m_id; }
    public String getM_name() { return m_name; }
    public String getRemaining_amount() { return remaining_amount; }
    public String getEnd_date() { return end_date; }


}
