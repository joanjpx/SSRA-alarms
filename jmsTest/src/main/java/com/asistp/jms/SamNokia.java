package com.asistp.jms;

/******************************************************************************
 *
 * $RCSfile: SamNokia.java,v $
 *
 ****************************************************************************
 *
 * $Revision: 1.6 $
 *
 * $Author: normanc $
 *
 ****************************************************************************
 *
 * Copyright (c) 2015 Alcatel-Lucent, Inc. All Rights Reserved.
 * Please read the associated COPYRIGHTS file for more details.
 *
 ****************************************************************************
 */

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import javax.jms.*;

public class SamNokia extends Thread implements ExceptionListener,
        MessageListener
{
    /**
     * Support for 5.0 external JMS context.
     */
    private static final String JMS_CONTEXT = "external/5620SamJMSServer";

    /**
     * The connection factory.
     */
    protected static final String CONNECTION_FACTORY = "SAMConnectionFactory";

    /**
     * The port seperator character for initial context construction.
     */
    protected static final char PORT_SEP_CHAR = ':';

    /**
     * The URL seperator character for initial context construction.
     */
    protected static final char URL_SEP_CHAR = '/';

    /**
     * The seperator character for initial context construction.
     */
    private static final char MULTI_SEP_CHAR = ',';

    /**
     * The list of available topics.
     * Note: These may not be correct based on configuration of the
     * attributes in nms-server.xml.
     */
    private static final String[] AVAILABLE_TOPICS = new String[]{
            "5620-MAP-topic", "5620-SAM-topic-xml-filtered", "5620-AUX-SAM-topic",
            "5650-CPAM-topic-xml", "5620-SAM-topic", "5620-GRAPH-topic",
            "5620-SAM-topic-xml", "5620-SAM-topic-xml-general", "5620-SAM-topic-xml-file",
            "5620-SAM-topic-xml-fault", "5620-SAM-topic-xml-stats"
    };

    /**
     * This is the topic string name.
     */
    protected String strName;

    /**
     * This is the URL string for a topic.
     */
    protected String strUrl;
    
    private String path;
    private String pathFileConfig;

    /**
     * This is the port for the topic.
     */
    protected String port;

    /**
     * This is the high availability URL string for a topic.
     */
    protected String strHaUrl;

    /**
     * This is the high availability port for the topic.
     */
    protected String haPort;

    /**
     * This is the client id for the topic.
     */
    protected String clientId;

    /**
     * This is the accepted client id for the topic. This attribute
     * contains the client id that was successfully registered with the server.
     */
    protected String acceptedClientId;

    /**
     * This is the user name for the topic.
     */
    protected String user;

    /**
     * This is the password of the user for the topic.
     */
    protected String password;

    /**
     * This is the message selector for the topic.
     */
    protected String filter;

    /**
     * This identifies if the consumer is listening to the topic.
     */
    protected boolean isListening;

    /**
     * This identifies if the consumer is connected.
     */
    protected boolean isConnected = false;

    /**
     * This identifies if the consumer has been stopped.
     */
    protected boolean isStopped = false;

    /**
     * The JNDI context of the connection.
     */
    private Context jndiContext = null;

    /**
     * The topic connection factory.
     */
    private TopicConnectionFactory topicConnectionFactory = null;

    /**
     * The topic connection.
     */
    private TopicConnection topicConnection = null;

    /**
     * The topic session.
     */
    private TopicSession topicSession = null;

    /**
     * The topic.
     */
    private Topic topic = null;

    /**
     * The topic subscriber.
     */
    private TopicSubscriber topicSubscriber = null;

    /**
     * Identifies if the subscriber is durable.
     */
    private boolean isPersistent = false;

    /**
     * Identifies if high availability is enabled.
     */
    private boolean isHaEnabled = true;

    /**
     * Counter for total number of messages.
     */
    private static int counter = 1;
    
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final Logger loggerExceptions = LogManager.getLogger("Exceptions");
    private static final Logger loggerInfo = LogManager.getLogger("Info");

    private static Map<String, String[]> codes = HashTable.getTable();
    /**
     * Constructor for creating.
     *
     * @param aInTopic The topic to connect to.
     * @param aInId The unique id for the connection.
     * @param aInUser The user to connect to the server with.
     * @param aInPassword The password for the user.
     * @param aInIsPersistent If the client is durable or not.
     * @param aInFilter The filter to use for the subscription.
     */
    
    public SamNokia( String aInTopic,  String aInId,  String aInUser,  String aInPassword,  boolean aInIsPersistent,  String aInFilter,  String path,  String pathFileConfig) {
        this.isConnected = false;
        this.isStopped = false;
        this.jndiContext = null;
        this.topicConnectionFactory = null;
        this.topicConnection = null;
        this.topicSession = null;
        this.topic = null;
        this.topicSubscriber = null;
        this.isPersistent = false;
        this.isHaEnabled = true;
        this.pathFileConfig = pathFileConfig;
        this.path = path;
        this.strName = aInTopic;
        this.clientId = aInId;
        this.user = aInUser;
        this.password = aInPassword;
        this.isPersistent = aInIsPersistent;
        this.filter = aInFilter;
        if (this.filter != null && this.filter.equals("")) {
            this.filter = "ALA_clientId in ('" + aInId + "','')";
            this.filter = "";
        }
    }

    /**
     * This method identifies if the JMS subscription is persistent.
     *
     * @return True if persistent, false if non-persistent.
     */
    public boolean isPersistent()
    {
        return isPersistent;
    }

    /**
     * This method is called by message service for each event received.
     *
     * @param aInMessage The event received
     */
    public void onMessage(Message aInMessage)
    {

        try
        {
            //System.out.println("Event: " + counter);
            if (aInMessage instanceof TextMessage)
            {
                //counter++;
                new Application().run(((TextMessage) aInMessage).getText(), codes,this.path,this.pathFileConfig);
                
                loggerInfo.warn(" Message: " + ((TextMessage) aInMessage).getText());
                
            }
            // The following
            // code allows the TextMessage to be unwrapped and processed as normal.
            // This will allow backwards compatability with previous versions of SAM.
            // NOTE: JMS header properties are contained in the Object Message, not the
            //        encapsulated TextMessage.
            else if (aInMessage instanceof ObjectMessage)
            {
                Object lObject = ((ObjectMessage) aInMessage).getObject();
                if (lObject != null && lObject instanceof Message)
                {
                    onMessage((Message) lObject);
                }
            }
            else
            {
                System.out.println("Invalid Message Type.");
            }
        }
        catch (Throwable e)
        {
            System.out.println("Exception: " + e.toString() + aInMessage);
        }
    }

    /**
     * This method is called to initialize the connection to the
     * server.
     *
     * @throws Exception The exception thrown if a conneciton error occurs.
     */
    public void initializeConnection() throws Exception
    {
        try
        {
            Hashtable env = new Hashtable();
            env.put(Context.SECURITY_PRINCIPAL, user );
            env.put(Context.SECURITY_CREDENTIALS, password);

            jndiContext = new InitialContext(env);

            //System.out.println("Initializing Topic (" + strName + ")...");
            try
            {

                topicConnectionFactory = (TopicConnectionFactory)
                        jndiContext.lookup(CONNECTION_FACTORY);
            }
            catch (Exception e)
            {
            	
                topicConnectionFactory = getExternalFactory(jndiContext);
            }

            // To use persistent JMS, the user must have durable subscription
            // permission (i.e. durable subscription role).
            if (user != null)
            {
                topicConnection = topicConnectionFactory.createTopicConnection(user, password);
                
                //System.out.println("Connection created for user: " + user);
            }
            else
            {
                topicConnection = topicConnectionFactory.createTopicConnection();
                System.out.println("Connection created.");
            }

            // Check for persistant JMS, if so, set the unique client id.
            // IMPORTANT: Client Id must be unique! In case of connection failure,
            // it identifies which messages this client missed.
            if ((isPersistent) && (null == clientId))
            {
                //System.out.println("Client ID cannot be null for a durable subscription.");
                throw new JMSException("Client ID cannot be null for a durable subscription.");
            }
            if ((null != clientId) && (!"".equals(clientId)))
            {
                topicConnection.setClientID(clientId);
                //System.out.println("Using client id: " + clientId);
            }

            // create the topic session.
            topicSession = topicConnection.createTopicSession(false,
                    TopicSession.AUTO_ACKNOWLEDGE);
            //System.out.println("Topic session created.");

            // find the topic.
            try
            {
                topic = (Topic) jndiContext.lookup(strName);
            }
            catch (NamingException ne)
            {
                // For SAM 5.0 support of the external JMS server.
                Context lInitialContext = (Context) jndiContext.lookup(JMS_CONTEXT);
                topic = (Topic) lInitialContext.lookup(strName);
            }
            //System.out.println("Finished initializing topic...");

            // create topic subscriber based on persistance.
            if (isPersistent)
            {
                // This is where the subscriber is created with durable subscription
                // for persistant JMS.  The client must specify a name that uniquely
                // identifies each durable subscription it creates.

                if (null != filter)
                {
                    topicSubscriber =
                            topicSession.createDurableSubscriber(topic, clientId, filter, false);
                    //System.out.println("Durable topic subscriber created with filter: " + filter);
                }
                else
                {
                    topicSubscriber =
                            topicSession.createDurableSubscriber(topic, clientId);
                    //System.out.println("Durable topic subscriber created.");
                }
            }
            else
            {
                if (null != filter)
                {
                    topicSubscriber = topicSession.createSubscriber(topic, filter, false);
                    //System.out.println("Topic subscriber created with filter: " + filter);
                }
                else
                {
                    topicSubscriber = topicSession.createSubscriber(topic);
                    //System.out.println("Topic subscriber created.");
                }
            }
            acceptedClientId = topicConnection.getClientID();
            logger.info("Connected and listening with Client ID: " + topicConnection.getClientID());
            setMessageListener(this);
            setExceptionListener(this);
            startListening();
            isConnected = true;
        }
        catch (Throwable jmse)
        {
            if (topicSession != null)
            {
                topicSession.close();
            }
            if (topicConnection != null)
            {
                topicConnection.close();
            }
            loggerExceptions.info(jmse.getMessage());
            isConnected = false;
            throw new JMSException(jmse.getMessage());
        }
        
    }

    /**
     * This method is used for the seperate SAM JMS server (SAM 5.0+).
     *
     * @param aInContext The initial context for the SAM server.
     *
     * @return The found connection factory.
     *
     * @throws NamingException If the factory could not be found.
     */
    private TopicConnectionFactory getExternalFactory(Context aInContext) throws NamingException
    {
        try
        {
            Context lInitialContext = (Context) aInContext.lookup(JMS_CONTEXT);
            return (TopicConnectionFactory) lInitialContext.lookup(CONNECTION_FACTORY);
        }
        catch (NamingException e)
        {
            loggerExceptions.info("JNDI API lookup failed: " + e.toString());
            throw e;
        }
    }

    /**
     * This method is called when the consumer wants to start
     * receiving messages.
     *
     * @throws JMSException If an exception happens on the connection.
     */
    public void startListening() throws JMSException
    {
        topicConnection.start();
        //System.out.println("Topic subscriber Listening...");
        isListening = true;
    }

    /**
     * This method is called when the consumer wants to stop receiving
     * messages.
     *
     * @throws JMSException If an exception happens on the connection.
     */
    public void stopListening() throws JMSException
    {
        if (null != topicConnection)
        {
            topicConnection.stop();
        }
        //System.out.println("Topic subscriber not Listening...");
        isListening = false;
    }

    /**
     * This method is called to unsubscribe from a durable subscription.  This
     * MUST be called to remove the subscription from the server otherwise
     * messages will be queued forever for this subscription.
     *
     * @throws JMSException If an error occurrs unsubscribing.
     */
    public void unsubscribe() throws JMSException
    {
        topicSession.unsubscribe(acceptedClientId);
    }

    /**
     * This method sets the exception listener on the connection.
     *
     * @param aInListener The connection listener to set.
     *
     * @throws JMSException If an exception happens on the connection.
     */
    public void setExceptionListener(ExceptionListener aInListener) throws JMSException
    {
        if (null != topicConnection)
        {
            topicConnection.setExceptionListener(aInListener);
        }
    }

    /**
     * This method sets the message listener for the connection.
     *
     * @param aInListener The message listener to receive messages.
     */
    public void setMessageListener(MessageListener aInListener)
    {
        try
        {
            topicSubscriber.setMessageListener(aInListener);
        }
        catch (Exception e)
        {
            logger.info("Exception setting message listener: " + e.getMessage());
        }
    }

    /**
     * This method is called when the consumer wants to close the
     * connection.
     */
    public synchronized void closeConnection()
    {
        close();
        isStopped = true;
    }

    /**
     * This method is called when the consumer wants to close the
     * connection.
     */
    private synchronized void close()
    {
        try
        {
            isConnected = false;
            stopListening();
            try
            {
                topicSubscriber.close();
            }
            catch (Exception e)
            {
                //System.out.println("Exception on subscriber close: " + e.getMessage());
            }
            if (isPersistent())
            {
                try
                {
                    unsubscribe();
                }
                catch (Exception e)
                {
                    //System.out.println("Exception on unsubscribe: " + e.getMessage());
                }
            }
            try
            {
                topicSession.close();
            }
            catch (Exception e)
            {
                //System.out.println("Exception on session close: " + e.getMessage());
            }
            try
            {
                topicConnection.close();
            }
            catch (Exception e)
            {
                //System.out.println("Exception on topic close: " + e.getMessage());
            }
            logger.info("Topic subscriber connection closed.");
        }
        catch (Exception e)
        {
            //System.out.println("Exception on close: " + e.getMessage());
        }
    }

    /**
     * This method is called when an exception occurrs on the JMS connection.
     *
     * @param aInException The exception that occurred.
     */
    public void onException(JMSException aInException)
    {
        logger.info("An Exception has occurred for the connection: " +
                aInException.getMessage());
        loggerExceptions.info(aInException.getMessage());
        try
        {
            setExceptionListener(null);
            //topicConnection.close();
            this.closeConnection();
        }
        catch (Exception e)
        {
            // Ignore this exception, the TCP connection may already be closed.
        }
        if (isHaEnabled)
        {
            int lAttempts = 0;
            boolean r = true; 
            //while (!isConnected && !isStopped)
            while (r)
            {
                lAttempts++;
                try
                {
                    initializeConnection();
                    r = false;
                    return;
                }
                catch (Exception e)
                {
                    logger.info("Connection Attempt #: " + lAttempts +
                            " Exception: " + e.getMessage());
                    loggerExceptions.info(e.getMessage());
                }
                try
                {
                    Thread.sleep(5000);
                }
                catch (Exception e)
                {
                    // This exception should not happen unless the process
                    // is killed at this point, in which case it is ignored.
                }
            }
        }
        else
        {
            System.out.println("Exiting...");
            System.exit(3);
        }
    }

    private static void printUsage()
    {
        System.out.println("Format: SamNokia -t <topic> -u <user> -p <password> {-f \"<filter>\"} {-persistent -c <uniqueid>}");
        System.out.println("  Mandatory Parameters:");
        System.out.println("      -t <topic> : The topic to connect to.");
        System.out.println("      -u <user> -p <password> : The user and password to login with.");
        System.out.println("  Optional Parameters:");
        System.out.println("      -f \"<filter>\" : The filter for messages (in SQL92 format)");
        System.out.println("      -persistent : Makes the connection durable.  If this option is taken, the client Id must be specified.");
        System.out.println("      -c <uniqueid> : The unique client id for this connection.");
        System.out.println("  Possible Topics:");
        for (int i = 0; i < AVAILABLE_TOPICS.length; i++)
        {
            System.out.println("      " + AVAILABLE_TOPICS[i]);
        }
        System.exit(1);
    }

    /**
     * This method parses the command line and either displays an error message or creates
     * a new instance of SamNokia.
     *
     * @param aInArgs The command line arguments.
     *
     * @return A new instance of SamNokia, or exits on error.
     *
     * @throws Exception if an error occurs parsing the command line.
     */
    public static SamNokia parseCommandLine(final String[] aInArgs) throws Exception {
        if (aInArgs.length < 1) {
            printUsage();
        }
        int lIndex = 0;
        String lTopic = null;
        String lUser = null;
        String lPassword = null;
        boolean lIsPersistent = false;
        String lFilter = "";
        String lClientId = "";
        String path = "";
        String pathFileConfig = "";
        while (lIndex < aInArgs.length) {
            if (aInArgs[lIndex].equals("-t")) {
                lTopic = aInArgs[++lIndex];
                ++lIndex;
            }
            else if (aInArgs[lIndex].equals("-u")) {
                lUser = aInArgs[++lIndex];
                ++lIndex;
            }
            else if (aInArgs[lIndex].equals("-p")) {
                lPassword = aInArgs[++lIndex];
                ++lIndex;
            }
            else if (aInArgs[lIndex].equals("-f")) {
                lFilter = aInArgs[++lIndex];
                ++lIndex;
            }
            else if (aInArgs[lIndex].equals("-persistent")) {
                lIsPersistent = true;
                ++lIndex;
            }
            else if (aInArgs[lIndex].equals("-c")) {
                lClientId = aInArgs[++lIndex];
                ++lIndex;
            }
            else if ("-help".equals(aInArgs[lIndex])) {
                printUsage();
            }
            else if ("-folder".equals(aInArgs[lIndex])) {
                path = aInArgs[++lIndex];
                ++lIndex;
            }
            else if ("-filebd".equals(aInArgs[lIndex])) {
                pathFileConfig = aInArgs[++lIndex];
                ++lIndex;
            }
            else {
                ++lIndex;
            }
        }
        return new SamNokia(lTopic, lClientId, lUser, lPassword, lIsPersistent, lFilter, path, pathFileConfig);
    }

    /**
     * This method prints the menu for the display.
     *
     * @param aInIsPersistent Identifies if the connection is persistent.
     * If so, it allows pausing/unpausing the connection.
     * @param aInIsPaused true if the connection is currently paused, false otherwise.
     */
    public static void printMenu(boolean aInIsPersistent, boolean aInIsPaused)
    {
        System.out.println("");
        System.out.println("r) Reset count");
        if (aInIsPersistent)
        {
            if (!aInIsPaused)
            {
                System.out.println("p) Pause Connection (for persistant JMS)");
            }
            else
            {
                System.out.println("u) Un-pause Connection (for persistant JMS)");
            }
        }
        System.out.println("q) Quit");
    }

    /**
     * Main entry to SamNokia.  It displays help if requested, or if activated
     * successfully, it will display a menu to the user.
     *
     * @param aInArgs Command line operands
     */
    public static void main(String[] aInArgs)
    {
    	
        boolean bContinue = false;
        Map<String, String[]> codes = HashTable.getTable();

        try
        {
            // determine SamNokia configuration based on command line arguments.
            SamNokia SamNokia = parseCommandLine(aInArgs);

            // connect to the server.
            SamNokia.initializeConnection();

        }
        catch (Exception e)
        {
            //System.out.println("Exception - " + e.getMessage());
            System.exit(1);
        }
    }
}//SamNokia