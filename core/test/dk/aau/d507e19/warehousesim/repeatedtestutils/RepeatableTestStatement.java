package dk.aau.d507e19.warehousesim.repeatedtestutils;

import org.junit.runners.model.Statement;

public class RepeatableTestStatement extends Statement
{
    private final int times;
    private final Statement statement;

    RepeatableTestStatement(int times, Statement statement)
    {
        this.times = times;
        this.statement = statement;
    }

    @Override
    public void evaluate() throws Throwable
    {
        for(int i = 0; i < times; i++)
        {
            statement.evaluate();
        }
    }
}