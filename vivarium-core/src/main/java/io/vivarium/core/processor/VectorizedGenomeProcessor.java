package io.vivarium.core.processor;

public interface VectorizedGenomeProcessor
{
    /**
     * Applies a processor defined normalization procedure to a processor.
     */
    public abstract void normalizeWeights(double normalizedLength);

    /**
     * Returns the length of the genome if measured as a high dimensional vector
     */
    public abstract double getGenomeLength();
}
