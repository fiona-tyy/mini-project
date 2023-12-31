export interface ExpenseData {
  transaction_type: string;
  description: string;
  date: number;
  who_paid: User;
  line_items: LineItem[];
  service_charge: number;
  gst: number;
  recorded_by: User;
  recorded_date: number;
  attachment: string;
}
export interface ExpenseProcessed {
  transaction_type: string;
  transaction_id: string;
  description: string;
  date: Date;
  who_paid: User;
  // line_items: LineItem[];
  // service_charge: number;
  // gst: number;
  recorded_by: User;
  recorded_date: Date;
  total_amount: number;
  shares_split: ShareSplit[];
  attachment: string;
}

export interface SettlementData {
  transaction_type: string;
  transaction_id?: string;
  description: string;
  date: number;
  recorded_by: User;
  recorded_date: number;
  repayment_amount: number;
  who_paid: User;
  who_received: User;
  attachment: string;
}

export interface ReceiptResponseData {
  description: string;
  date: number;
  service_charge: number;
  gst: number;
  line_items: LineItem[];
}

export interface LineItem {
  item: string;
  amount: number;
  split_with?: string[];
}

export interface ShareSplit {
  email: string;
  name: string;
  share_amount: number;
}

export interface User {
  email: string;
  name: string;
}

export interface Friend {
  email: string;
  name: string;
  amount_outstanding: number;
}

export interface Transaction {
  transaction_type: string;
  transaction_id: string;
  description: string;
  date: number;
  who_paid: User;
  total_amount: number;
  who_borrowed: ShareSplit;
  recorded_by: User;
  recorded_date: number;
  attachment: string;
}

export interface UserDTO {
  name: string;
  email: string;
  token: string;
  token_expiration_date: number;
  google_token?: string;
}

export interface NotificationModel {
  title: string;
  body: string;
  isVisible: boolean;
}
