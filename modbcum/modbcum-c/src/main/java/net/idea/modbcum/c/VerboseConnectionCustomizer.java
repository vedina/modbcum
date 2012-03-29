package net.idea.modbcum.c;

import java.sql.Connection;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

public class VerboseConnectionCustomizer extends AbstractConnectionCustomizer  {
    public void onAcquire( Connection c, String pdsIdt )
    { 
       System.err.println(String.format("Acquired %s [%s]",c,pdsIdt)); 
       // override the default transaction isolation of 
       // newly acquired Connections
       // c.setTransactionIsolation( Connection.REPEATABLE_READ );
    }

    public void onDestroy( Connection c, String pdsIdt ) { 
    	System.err.println(String.format("Destroying %s [%s]",c,pdsIdt)); 
    }

    public void onCheckOut( Connection c, String pdsIdt ) {
    	System.err.println(String.format("Checked out %s [%s]",c,pdsIdt));
    }

    public void onCheckIn( Connection c, String pdsIdt ) {
    	System.err.println(String.format("Checked in %s [%s]",c,pdsIdt));
    }
}
