package io.vivarium.audit;

public enum AuditType
{
    ACTION_FREQUENCY
    {
        @Override
        public ActionFrequencyBlueprint makeAuditBlueprint()
        {
            return new ActionFrequencyBlueprint();
        }
    },
    BREEDING_PATTERN
    {
        @Override
        public BreedingPatternBlueprint makeAuditBlueprint()
        {
            return new BreedingPatternBlueprint();
        }
    },
    CENSUS
    {
        @Override
        public CensusBlueprint makeAuditBlueprint()
        {
            return new CensusBlueprint();
        }
    },
    MEMORIAL
    {
        @Override
        public AuditBlueprint makeAuditBlueprint()
        {
            throw new UnsupportedOperationException("CreatureMemorialFunction is not implemented");
        }
    };

    public abstract AuditBlueprint makeAuditBlueprint();
}
