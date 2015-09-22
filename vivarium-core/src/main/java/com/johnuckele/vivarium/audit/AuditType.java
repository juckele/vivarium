package com.johnuckele.vivarium.audit;

import java.util.Map;

import com.johnuckele.vivarium.core.Species;

public enum AuditType
{
    ACTION_FREQUENCY
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return ActionFrequencyRecord.class;
        }

        @Override
        public ActionFrequencyRecord makeRecordWithSpecies(AuditFunction function, Species species)
        {
            return ActionFrequencyRecord.makeWithSpecies(function, species);
        }

        @Override
        public AuditFunction makeFunctionFromMap(Map<String, Object> functionOptions)
        {
            return ActionFrequencyFunction.makeFromMap(functionOptions);
        }
    },
    CENSUS
    {
        @Override
        public Class<?> getAuditRecordClass()
        {
            return CensusRecord.class;
        }

        @Override
        public CensusRecord makeRecordWithSpecies(AuditFunction untypedFunction, Species species)
        {
            CensusFunction censusFunction = (CensusFunction) untypedFunction;
            return CensusRecord.makeWithSpecies(censusFunction, species);
        }

        @Override
        public CensusFunction makeFunctionFromMap(Map<String, Object> functionOptions)
        {
            CensusFunction censusFunction = CensusFunction.makeFromMap(functionOptions);
            return censusFunction;
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
        public AuditRecord makeRecordWithSpecies(AuditFunction function, Species species)
        {
            return CreatureMemorial.makeWithSpecies(function, species);
        }

        @Override
        public AuditFunction makeFunctionFromMap(Map<String, Object> functionOptions)
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("X");
        }
    };

    public abstract Class<?> getAuditRecordClass();

    public abstract AuditRecord makeRecordWithSpecies(AuditFunction function, Species species);

    public abstract AuditFunction makeFunctionFromMap(Map<String, Object> functionOptions);
}
