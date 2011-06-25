#pragma once


// CTranDlg dialog

class CTranDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CTranDlg)

public:
	CTranDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CTranDlg();

// Dialog Data
	enum { IDD = IDD_DIALOG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
};
