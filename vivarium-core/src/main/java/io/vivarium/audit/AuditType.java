package io.vivarium.audit;

import io.vivarium.core.Species;

public enum AuditType
{
    ACTION_FREQUENCY
    {
        @Override
        public ActionFrequencyFunction makeFunction()
        {
            return new ActionFrequencyFunction();
        }

        @Override
        public ActionFrequencyRecord makeRecordWithSpecies(AuditFunction function, Species species)
        {
            return new ActionFrequencyRecord(species);
        }
    },
    CENSUS
    {
        @Override
        public CensusFunction makeFunction()
        {
            return new CensusFunction();
        }

        @Override
        public CensusRecord makeRecordWithSpecies(AuditFunction untypedFunction, Species species)
        {
            CensusFunction censusFunction = (CensusFunction) untypedFunction;
            return new CensusRecord(censusFunction, species);
        }
    },
    MEMORIAL
    {
        @Override
        public AuditFunction makeFunction()
        {
            throw new UnsupportedOperationException("CreatureMemorialFunction does not exist");
        }

        @Override
        public AuditRecord makeRecordWithSpecies(AuditFunction function, Species species)
        {
            return new CreatureMemorial(species);
        }
    };

    public abstract AuditFunction makeFunction();

    public abstract AuditRecord makeRecordWithSpecies(AuditFunction function, Species species);
}
