
// CrazySpotHelperDlg.h : header file
//

#pragma once
#include "TranDlg.h"
#include <time.h>
#include <vector>

// CCrazySpotHelperDlg dialog
class CCrazySpotHelperDlg : public CDialogEx
{
// Construction
public:
	CCrazySpotHelperDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_CRAZYSPOTHELPER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
	
public:
	CWnd* m_pGame;
	bool GetGameHandle(void);
	bool GetPicByCap(CBitmap &bitmap_left, CBitmap &bitmap_right, CBitmap &bitmap_buffer);
	void Compare_Ex(CBitmap &bitmap_left,CBitmap &bitmap_right, CImage* &pBuffer);
	afx_msg void OnBnClickedCancel();
	afx_msg void OnBnClickedOk();
	void ShowFault(const CImage* pBuffer);
	bool ImageCopy(const CImage &srcImage,CImage &destImage);
private:
	CTranDlg* m_pDlg;

	
};
