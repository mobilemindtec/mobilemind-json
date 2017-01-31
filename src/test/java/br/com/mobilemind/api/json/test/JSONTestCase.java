package br.com.mobilemind.api.json.test;

/*
 * #%L
 * Mobile Mind - JSON
 * %%
 * Copyright (C) 2012 Mobile Mind Empresa de Tecnologia
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import br.com.mobilemind.api.json.JSON;
import br.com.mobilemind.api.json.JSONArray;
import br.com.mobilemind.api.json.JSONException;
import br.com.mobilemind.api.json.JSONObject;
import br.com.mobilemind.api.json.test.tools.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ricardo Bocchi
 */
public class JSONTestCase {

    public JSONTestCase() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPersonToJSON() throws JSONException {

        //String expected = "{\"status\":true,\"complexObject\":{\"name\":\"Ricardo\"},\"type\":\"F\",\"age\":50,\"name\":\"JSON Boy\",\"date\":\"31/07/2012\",\"year\":1.2,\"id\":5}";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

        Person person = new Person();

        //expected = String.format(expected, sf.format(person.getDate()));

        JSON< Person> json = new JSON<Person>(Person.class);
        json.setEntity(person);
        JSONObject jsonObject = json.toJSON();
        Assert.assertNotNull(jsonObject);

        //String jsonText = jsonObject.toString();

        // Assert.assertEquals(expected, jsonText);

        Person aux = json.fromJSON(jsonObject);

        Assert.assertEquals(person.getAge(), aux.getAge());
        Assert.assertEquals(person.getId(), aux.getId());
        Assert.assertEquals(person.getName(), aux.getName());
        Assert.assertEquals(person.getType(), aux.getType());
        Assert.assertEquals(person.getYear(), aux.getYear());
        Assert.assertEquals(sf.format(person.getDate()), sf.format(aux.getDate()));
        Assert.assertEquals(person.getComplexObject(), aux.getComplexObject());
    }

    @Test
    public void testJSONToPerson() throws JSONException {
        String expected = "{\"id\":5,\"complexObject\":{\"name\":\"Ricardo\"},\"status\":true,\"age\":50,\"name\":\"JSON Boy\",\"year\":1.2,\"date\":\"%s\",\"type\":\"F\"}";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

        Person person = new Person();
        expected = String.format(expected, sf.format(person.getDate()));

        JSON<Person> json = new JSON<Person>(Person.class);


        JSONObject obj = new JSONObject(expected);

        Person aux = json.fromJSON(obj);

        Assert.assertEquals(person.getAge(), aux.getAge());
        Assert.assertEquals(person.getId(), aux.getId());
        Assert.assertEquals(person.getName(), aux.getName());
        Assert.assertEquals(person.getType(), aux.getType());
        Assert.assertEquals(person.getYear(), aux.getYear());
        Assert.assertEquals(sf.format(person.getDate()), sf.format(aux.getDate()));
        Assert.assertEquals(person.getComplexObject(), aux.getComplexObject());
    }

    @Test
    public void testToJSONArray() throws JSONException {
        //      String expected = "[{\"id\":5,\"complexObject\":{\"name\":\"Ricardo\"},\"status\":true,\"age\":50,\"name\":\"JSON Boy\",\"year\":1.2,\"date\":\"%s\",\"type\":\"F\"},{\"id\":5,\"complexObject\":{\"name\":\"Ricardo\"},\"status\":true,\"age\":50,\"name\":\"JSON Boy\",\"year\":1.2,\"date\":\"%s\",\"type\":\"F\"}]";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        List<Person> persons = new LinkedList<Person>();
        persons.add(new Person());
        persons.add(new Person());

        //    expected = String.format(expected, sf.format(persons.get(0).getDate()), sf.format(persons.get(1).getDate()));

        JSON<Person> json = new JSON<Person>(Person.class);

        JSONArray array = json.toJSONArray(persons);

        String value = array.toString();


//        Assert.assertEquals(expected, value);


        List<Person> items = json.fromJSONArray(value);

        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(persons.get(i).getAge(), items.get(i).getAge());
            Assert.assertEquals(persons.get(i).getId(), items.get(i).getId());
            Assert.assertEquals(persons.get(i).getName(), items.get(i).getName());
            Assert.assertEquals(persons.get(i).getType(), items.get(i).getType());
            Assert.assertEquals(persons.get(i).getYear(), items.get(i).getYear());
            Assert.assertEquals(sf.format(persons.get(i).getDate()), sf.format(items.get(i).getDate()));
            Assert.assertEquals(persons.get(i).getComplexObject(), items.get(i).getComplexObject());
        }

    }

    @Test
    public void testJSONArrayToPerson() throws JSONException {
        Date date = new Date();
        String expected = "[{\"id\":5,\"complexObject\":{\"name\":\"Ricardo\"},\"status\":true,\"age\":50,\"name\":\"JSON Boy\",\"year\":1.2,\"date\":\"%s\",\"type\":\"F\"},{\"id\":5,\"complexObject\":{\"name\":\"Ricardo\"},\"status\":true,\"age\":50,\"name\":\"JSON Boy\",\"year\":1.2,\"date\":\"%s\",\"type\":\"F\"}]";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        expected = String.format(expected, sf.format(date), sf.format(date));

        JSON<Person> json = new JSON<Person>(Person.class);
        List<Person> persons = json.fromJSONArray(expected);

        List<Person> expecteds = new LinkedList<Person>();
        expecteds.add(new Person());
        expecteds.add(new Person());

        for (int i = 0; i < 2; i++) {
            expecteds.get(i).setDate(date);
            Assert.assertEquals(expecteds.get(i).getAge(), persons.get(i).getAge());
            Assert.assertEquals(expecteds.get(i).getId(), persons.get(i).getId());
            Assert.assertEquals(expecteds.get(i).getName(), persons.get(i).getName());
            Assert.assertEquals(expecteds.get(i).getType(), persons.get(i).getType());
            Assert.assertEquals(expecteds.get(i).getYear(), persons.get(i).getYear());
            Assert.assertEquals(sf.format(expecteds.get(i).getDate()), sf.format(persons.get(i).getDate()));
            Assert.assertEquals(expecteds.get(i).getComplexObject(), persons.get(i).getComplexObject());
        }
    }

    @Test
    public void testInjectAndGetValueProperties() {
        MethodInject injetc = new MethodInject();

        JSON<MethodInject> json = new JSON<MethodInject>(MethodInject.class);

        String text = json.toJSON(injetc).toString();

        Assert.assertTrue("leitura do valor deveria ser feito get method", injetc.injectedGet);

        MethodInject other = json.fromJSON(text);

        Assert.assertTrue("escrita do valor deveria ser feito set method", other.injectedSet);
    }
    @Test
    public void testObjetoComListGenericaInformandoTipoNaAnotada() {
        ObjectWithListGenericAnnoted it = new ObjectWithListGenericAnnoted();
        it.getItems().add(new ComplexObject());

        JSON<ObjectWithListGenericAnnoted> json = new JSON<ObjectWithListGenericAnnoted>(ObjectWithListGenericAnnoted.class);

        String result = json.toJSON(it).toString();

        Assert.assertEquals("{\"items\":[{\"name\":\"Ricardo\"}]}", result);

    }

    @Test
    public void testObjetoComListGenericaSemInformarTipoNaAnotacao() {
        ObjectWithoutListGenericAnnoted it = new ObjectWithoutListGenericAnnoted();
        it.getItems().add(new ComplexObject());

        JSON<ObjectWithoutListGenericAnnoted> json = new JSON<ObjectWithoutListGenericAnnoted>(ObjectWithoutListGenericAnnoted.class);

        String result = json.toJSON(it).toString();

        Assert.assertEquals("{\"items\":[{\"name\":\"Ricardo\"}]}", result);

    }

    @Test(expected = JSONException.class)
    public void testObjetoComListDeObject() {
        ObjectWithListObject it = new ObjectWithListObject();
        it.getItems().add(new ComplexObject());

        JSON<ObjectWithListObject> json = new JSON<ObjectWithListObject>(ObjectWithListObject.class);

        json.toJSON(it).toString();

    }
    
    @Test
    public void testListString(){
        StringList list = new StringList();
        JSON<StringList> json = new JSON<StringList>(StringList.class);
        String  value = json.toJSON(list).toString();
        
        Assert.assertEquals("{\"items\":[\"A\",\"B\",\"C\"]}", value);
        
        StringList other = json.fromJSON(value);
    }
}
