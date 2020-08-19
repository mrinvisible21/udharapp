package com.example.udhar.AllMemberLists;


public class AllMember {


   private  String id,u_id,m_name,m_mobile,m_address,m_status;

    public AllMember(String m_name,String m_mobile) {

     this.m_name= m_name;
     this.m_mobile= m_mobile;
    }

    public String getId() { return id; }
    public String getM_name() { return m_name; }
    public String getM_mobile() { return m_mobile; }
    public String getM_address() { return m_address; }


}
