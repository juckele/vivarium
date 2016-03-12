package io.vivarium.audit;

public enum AuditType
{
    ACTION_FREQUENCY
    {
        @Override
        public ActionFrequencyFunction makeFunction()
        {
            return new ActionFrequencyFunction();
        }
    },
    CENSUS
    {
        @Override
        public CensusFunction makeFunction()
        {
            return new CensusFunction();
        }
    },
    MEMORIAL
    {
        @Override
        public AuditFunction makeFunction()
        {
            throw new UnsupportedOperationException("CreatureMemorialFunction is not implemented");
        }
    };

    public abstract AuditFunction makeFunction();
}
