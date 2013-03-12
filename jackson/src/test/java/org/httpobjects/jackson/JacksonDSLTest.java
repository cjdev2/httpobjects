package org.httpobjects.jackson;

import org.httpobjects.Representation;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JacksonDSLTest {

    private RandomBean randomBean;
    private String representationText;

    class RandomBean {
        String message;

        RandomBean(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RandomBean)) return false;

            RandomBean that = (RandomBean) o;

            return !(message != null ? !message.equals(that.message) : that.message != null);

        }

        @Override
        public int hashCode() {
            return message != null ? message.hashCode() : 0;
        }
    }

    @Before
    public void setUp() throws Exception {
        randomBean = new RandomBean("Hello");
        representationText = "{\"message\":\"Hello\"}";
    }

    @Test
    public void jacksonJson() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JacksonDSL.JacksonJson(randomBean).write(outputStream);
        assertEquals(outputStream.toString(), representationText);
    }

    @Test
    public void representationConverter() throws IOException {
        Representation representation = JacksonDSL.JacksonJson(randomBean);
        RandomBean returnedBean = JacksonDSL.convertRepresentation(representation).to(RandomBean.class);
        assertEquals(randomBean, returnedBean);
    }


}
