/*************************************************************************
 *                                                                       *
 * Open Dynamics Engine, Copyright (C) 2001,2002 Russell L. Smith.       *
 * All rights reserved.  Email: russ@q12.org   Web: www.q12.org          *
 *                                                                       *
 * This library is free software; you can redistribute it and/or         *
 * modify it under the terms of EITHER:                                  *
 *   (1) The GNU Lesser General Public License as published by the Free  *
 *       Software Foundation; either version 2.1 of the License, or (at  *
 *       your option) any later version. The text of the GNU Lesser      *
 *       General Public License is included with this library in the     *
 *       file LICENSE.TXT.                                               *
 *   (2) The BSD-style license that is included with this library in     *
 *       the file LICENSE-BSD.TXT.                                       *
 *                                                                       *
 * This library is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files    *
 * LICENSE.TXT and LICENSE-BSD.TXT for more details.                     *
 *                                                                       *
 *************************************************************************/
package org.ode4j.ode.internal.joints;

import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBallJoint;
import org.ode4j.ode.internal.DxWorld;
import org.ode4j.ode.internal.Common.D_PARAM_NAMES;
import org.ode4j.ode.internal.Common.D_PARAM_NAMES_N;


/** 
 * ****************************************************************************
 * ball and socket.
 */
public class DxJointBall extends DxJoint implements DBallJoint
{
	DVector3 anchor1;   // anchor w.r.t first body
	DVector3 anchor2;   // anchor w.r.t second body
	double erp;          // error reduction
	double cfm;          // constraint force mix in

	DxJointBall( DxWorld w ) {
		super( w );
		anchor1 = new DVector3();
		anchor2 = new DVector3();
		//    MAT.dSetZero( anchor1, 4 );
		//    MAT.dSetZero( anchor2, 4 );
		erp = world.global_erp;
		cfm = world.global_cfm;
	}


	public void
	getInfo1( DxJoint.Info1 info )
	{
		info.setM(3);
		info.setNub(3);
	}


	public void
	getInfo2( DxJoint.Info2 info )
	{
		info.erp = erp;
		info.setCfm(0, cfm);
		info.setCfm(1, cfm);
		info.setCfm(2, cfm);
		//    info.cfm.set(cfm, cfm, cfm);
		setBall( this, info, anchor1, anchor2 );
	}


	//void dJointSetBallAnchor( dJoint j, double x, double y, double z )
	public void dJointSetBallAnchor( double x, double y, double z )
	{
		dJointSetBallAnchor( new DVector3(x, y, z) );
	}
	public void dJointSetBallAnchor( DVector3C xyz )
	{
		setAnchors( xyz, anchor1, anchor2 );
		//TODO TZ: Why not computeInitialRelativeRotations(); ??? Like in other joints?
	}


	//void dJointSetBallAnchor2( dJoint j, double x, double y, double z )
	void dJointSetBallAnchor2( double x, double y, double z )
	{
		//    joint.anchor2.v[0] = x;
		//    joint.anchor2.v[1] = y;
		//    joint.anchor2.v[2] = z;
		//    joint.anchor2.v[3] = 0;
		anchor2.set(x, y, z);
	}

	//void dJointGetBallAnchor( dJoint j, dVector3 result )
	void dJointGetBallAnchor( DVector3 result )
	{
		if ( (flags & dJOINT_REVERSE) != 0 )
			getAnchor2( result, anchor2 );
		else
			getAnchor( result, anchor1 );
	}


	//void dJointGetBallAnchor2( dJoint j, dVector3 result )
	void dJointGetBallAnchor2( DVector3 result )
	{
		if ( (flags & dJOINT_REVERSE) != 0 )
			getAnchor( result, anchor1 );
		else
			getAnchor2( result, anchor2 );
	}


	void set( D_PARAM_NAMES num, double value )
	{
		switch ( num )
		{
		case dParamCFM:
			cfm = value;
			break;
		case dParamERP:
			erp = value;
			break;
		}
	}


	double get( D_PARAM_NAMES num )
	{
		switch ( num )
		{
		case dParamCFM:
			return cfm;
		case dParamERP:
			return erp;
		default:
			return 0;
		}
	}


	void dJointSetBallParam( D_PARAM_NAMES parameter, double value )
	{
		set( parameter, value );
	}


	double dJointGetBallParam( D_PARAM_NAMES parameter )
	{
		return get( parameter );
	}


	// *******************************
	// API dBallJoint
	// *******************************

	public final void setAnchor (double x, double y, double z)
	{ dJointSetBallAnchor (x, y, z); }
	public final void setAnchor (final DVector3C a)
	{ dJointSetBallAnchor (a); }
	public final void getAnchor (DVector3 result)
	{ dJointGetBallAnchor (result); }
	public final void getAnchor2 (DVector3 result)
	{ dJointGetBallAnchor2 (result); }
	public final void setParam (D_PARAM_NAMES_N parameter, double value)
	{ 
		if (!parameter.isGroup1()) 
		throw new IllegalArgumentException("Only Group #1 allowed, but got: " + parameter.name());
		dJointSetBallParam (parameter.toSUB(), value); 
	}
	public final double getParam (D_PARAM_NAMES_N parameter)
	{ 	
		if (!parameter.isGroup1()) 
		throw new IllegalArgumentException("Only Group #1 allowed, but got: " + parameter.name());
		return dJointGetBallParam (parameter.toSUB()); 
	}
}