## Interactivity in Lets-Plot: Pan, Zoom, and Selection Tools
Architectural Decision Record

Context:

The Lets-Plot library aims to provide interactive plotting capabilities, including panning, zooming, and data selection tools.

Since Lets-Plot currently lacks an extensive JavaScript API for controlling all aspects of rendering during user interactions, 
the implementation of these interactive features needs to consider this limitation.

However, a more comprehensive JavaScript API can be added in the future as necessary.

Decision:

In Lets-Plot, the Pan, Zoom, and Selection tools will follow a simplified event-driven architecture.

There will be no intermediate events triggered during user interactions. 
The internal event loop will handle the rendering updates, and only the final state or result will be exposed through API events.

### The tool lifecycle
The tool lifecycle will include the following stages:

#### 1. Tool Activation
When a tool is activated (e.g., by clicking a toolbar button or programmatically), event listeners will be bound 
to the relevant DOM elements (e.g., canvas, document) to capture user interactions.

#### 2. Internal Event Loop
During user interactions (e.g., mouse down, move, up), an internal event loop will handle the respective events.
The internal event loop will update the plot view as the user interacts with the plot.

#### 3. Interaction End or Cancel

**End:** Upon completion of the interaction (e.g., mouseup), an "end" event will be triggered, 
providing the final state or result of the interaction 
(e.g., final axis ranges after panning/zooming, selected data points after selection).

Upon the "end" event:

- Pan and Zoom: The tool will update the plot view with the final axis ranges provided by the event.
- Selection tool: The tool will visually indicate the selected data points provided by the event. 
  See "JavaScript Event Propagation to Python Application" below for further details.

**Cancel:** If the interaction is canceled (e.g., escape key pressed), the internal event loop will first 
handle the cancellation and revert the plot view to its pre-interaction state. 

Then a "cancel" event will be triggered, informing the tool that the interaction has been canceled.

#### 4. After "End" or "Cancel"
After the interaction ends or is canceled, there are two possible options:
- The tool remains active and ready for further interactions until the user explicitly deactivates it or selects another tool.
- The tool is automatically deactivated, and the "Event Cleanup" step is performed.

#### 5. Event Cleanup
After the tool is deactivated, the event listeners bound during activation will be removed.


### JavaScript Event Propagation to Python Application

In order for the Python application to respond to user interactions, 
the events triggered by the Pan, Zoom, and Selection tools in the JavaScript layer need to be propagated to the Python layer.

The communication from the JavaScript layer to the Python layer will be handled through an event broker running in the Python application.

#### Python Event Broker
The Python application will host an event broker that listens for incoming events from the JavaScript layer. 

This event broker will be responsible for receiving the event data and passing it to the appropriate event handlers 
in the Python application.

#### Event Transmission
When an event (e.g., "end" or "cancel") is triggered in the JavaScript layer, 
the relevant event data (e.g., final axis ranges, selected data points) will be serialized into a format
suitable for transmission, such as JSON.

The serialized event data will then be sent to the Python event broker through a communication channel, 
such as a WebSocket or an HTTP request.

Upon receiving the serialized event data, the Python event broker will deserialize it into a usable format, 
such as Python objects or data structures.

#### Event Handling in Python:
The deserialized event data will be passed to the appropriate callback functions in the Python application. 
These handlers will perform any necessary actions, such as triggering additional computations or updating visualizations.