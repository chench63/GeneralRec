
// CrazySpotHelper.h : main header file for the PROJECT_NAME application
//

#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

#include "resource.h"		// main symbols


// CCrazySpotHelperApp:
// See CrazySpotHelper.cpp for the implementation of this class
//

class CCrazySpotHelperApp : public CWinApp
{
public:
	CCrazySpotHelperApp();

// Overrides
public:
	virtual BOOL InitInstance();

// Implementation

	DECLARE_MESSAGE_MAP()
};

extern CCrazySpotHelperApp theApp;