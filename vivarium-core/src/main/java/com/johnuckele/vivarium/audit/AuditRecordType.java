package com.johnuckele.vivarium.audit;

import com.johnuckele.vivarium.core.Species;

public enum AuditRecordType
{
    ACTION_FREQUENCY
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return ActionFrequency.class;
        }

        @Override
        public ActionFrequency makeWithSpecies(Species species)
        {
            return ActionFrequency.makeWithSpecies(species);
        }
    },
    CENSUS
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return Census.class;
        }

        @Override
        public Census makeWithSpecies(Species species)
        {
            return Census.makeWithSpecies(species);
        }
    },
    MEMORIAL
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return CreatureMemorial.class;
        }

        @Override
        public AuditRecord makeWithSpecies(Species species)
        {
            return CreatureMemorial.makeWithSpecies(species);
        }
    };

    public abstract Class<?> getAuditRecordClass();

    public abstract AuditRecord makeWithSpecies(Species species);
}
