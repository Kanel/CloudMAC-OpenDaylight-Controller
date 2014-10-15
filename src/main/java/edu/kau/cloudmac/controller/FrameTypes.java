package edu.kau.cloudmac.controller;

public enum FrameTypes
{
	Management_Association_request(0b00000000),
	Management_Association_Response(0b00010000),
	Management_Reassociation_request(0b00100000),
	Management_Reassociation_Response(0b00110000),
	Management_Probe_Request(0b01000000),
	Management_Probe_Response(0b01010000),
	Management_Timing_Advertisement(0b01100000),
	Management_Reserved(0b01110000),
	Management_Beacon(0b10000000),
	Management_ATIM(0b10010000),
	Management_Disassociation(0b10100000),
	Management_Authentication(0b10110000),
	Management_Deauthentication(0b11000000),
	Management_Action(0b11010000),
	Management_Action_No_Ack(0b11100000),

	Control_Control_Wrapper(0b01110100),
	Control_Block_Ack_Request(0b10000100),
	Control_Block_Ack(0b10010100),
	Control_PS_Poll(0b10100100),
	Control_RTS(0b10110100),
	Control_CTS(0b11000100),
	Control_ACK(0b11010100),
	Control_CF_End(0b11100100),
	Control_CF_End_Plus_CF_Ack(0b11110100),

	Data_Data(0b00001000),
	Data_Data_Plus_CF_Ack(0b00011000),
	Data_Data_Plus_CF_Poll(0b00101000),
	Data_Data_Plus_CF_Ack_Plus_CF_Poll(0b00111000),
	Data_Null_No_Data(0b01001000),
	Data_CF_Ack_No_Data(0b01011000),
	Data_CF_Poll_No_Data(0b01101000),
	Data_CF_Ack_Plus_CF_Poll_No_Data(0b01111000),
	Data_QoS_Data(0b10001000),
	Data_QoS_Data_Plus_CF_Ack(0b10011000),
	Data_QoS_Data_Plus_CF_Poll(0b10101000),
	Data_QoS_Data_Plus_CF_Ack_Plus_CF_Poll(0b10111000),
	Data_QoS_Null_No_Data(0b11001000),
	Data_QoS_CF_Poll_No_Data(0b11101000),
	Data_QoS_CF_Ack_Plus_CF_Poll_No_Data(0b11111000),

	Cheese(0b00001100); // Reserved, Also http://www.youtube.com/watch?v=B_m17HK97M8;



	private int value;

        private FrameTypes(int value) {
            this.value = value;
        }

	// JAVA!?!?=!
	public static FrameTypes lookup(int lookupValue)
	{
		switch (lookupValue)
                {
			case 0b00000000:
				return  Management_Association_request;

			case 0b00010000:
				return Management_Association_Response;

			case 0b00100000:
				return Management_Reassociation_request;

			case 0b00110000:
				return Management_Reassociation_Response;

			case 0b01000000:
				return Management_Probe_Request;

			case 0b01010000:
				return Management_Probe_Response;

			case 0b01100000:
				return Management_Timing_Advertisement;

			case 0b01110000:
				return Management_Reserved;

			case 0b10000000:
				return Management_Beacon;

			case 0b10010000:
				return Management_ATIM;

			case 0b10100000:
				return Management_Disassociation;

			case 0b10110000:
				return Management_Authentication;

			case 0b11000000:
				return Management_Deauthentication;

			case 0b11010000:
				return Management_Action;

			case 0b11100000:
				return Management_Action_No_Ack;

			case 0b01110100:
				return Control_Control_Wrapper;

			case 0b10000100:
				return Control_Block_Ack_Request;

			case 0b10010100:
				return Control_Block_Ack;

			case 0b10100100:
				return Control_PS_Poll;

			case 0b10110100:
				return Control_RTS;

			case 0b11000100:
				return Control_CTS;

			case 0b11010100:
				return Control_ACK;

			case 0b11100100:
				return Control_CF_End;

			case 0b11110100:
				return Control_CF_End_Plus_CF_Ack;

			case 0b00001000:
				return Data_Data;

			case 0b00011000:
				return Data_Data_Plus_CF_Ack;

			case 0b00101000:
				return Data_Data_Plus_CF_Poll;

			case 0b00111000:
				return Data_Data_Plus_CF_Ack_Plus_CF_Poll;

			case 0b01001000:
				return Data_Null_No_Data;

			case 0b01011000:
				return Data_CF_Ack_No_Data;

			case 0b01101000:
				return Data_CF_Poll_No_Data;

			case 0b01111000:
				return Data_CF_Ack_Plus_CF_Poll_No_Data;

			case 0b10001000:
				return Data_QoS_Data;

			case 0b10011000:
				return Data_QoS_Data_Plus_CF_Ack;

			case 0b10101000:
				return Data_QoS_Data_Plus_CF_Poll;

			case 0b10111000:
				return Data_QoS_Data_Plus_CF_Ack_Plus_CF_Poll;

			case 0b11001000:
				return Data_QoS_Null_No_Data;

			case 0b11101000:
				return Data_QoS_CF_Poll_No_Data;

			case 0b11111000:
				return Data_QoS_CF_Ack_Plus_CF_Poll_No_Data;

			default:
				return Cheese;
		}
	}
}
