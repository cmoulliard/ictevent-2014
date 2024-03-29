package org.devnation.camel;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.camel.Exchange;

public interface DataFormat {

    void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception;

    Object unmarshal(Exchange exchange, InputStream stream) throws Exception;
}