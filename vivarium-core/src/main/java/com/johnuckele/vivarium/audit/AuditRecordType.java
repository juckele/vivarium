package com.johnuckele.vivarium.audit;

public enum AuditRecordType
{
    ACTION_FREQUENCY
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return ActionFrequency.class;
        }
    },
    CENSUS
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return Census.class;
        }
    },
    MEMORIAL
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return CreatureMemorial.class;
        }
    };

    public abstract Class<?> getAuditRecordClass();
}
