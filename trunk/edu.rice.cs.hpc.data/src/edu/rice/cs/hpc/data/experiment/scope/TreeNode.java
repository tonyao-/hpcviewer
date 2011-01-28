package edu.rice.cs.hpc.data.experiment.scope;

/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


/**
 * A simple data structure that is useful for implemented tree models. This can
 * be returned by
 * {@link org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)}.
 * It allows simple delegation of methods from
 * {@link org.eclipse.jface.viewers.ITreeContentProvider} such as
 * {@link org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)},
 * {@link org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)} and
 * {@link org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)}
 * 
 * @since 3.2
 */
public class TreeNode {

	/**
	 * The array of child tree nodes for this tree node. If there are no
	 * children, then this value may either by an empty array or
	 * <code>null</code>. There should be no <code>null</code> children in
	 * the array.
	 */
	private TreeNode[] children;

	/**
	 * The parent tree node for this tree node. This value may be
	 * <code>null</code> if there is no parent.
	 */
	private TreeNode parent;

	/**
	 * The value contained in this node. This value may be anything.
	 */
	protected Object value;

	/**
	 * Constructs a new instance of <code>TreeNode</code>.
	 * 
	 * @param value
	 *            The value held by this node; may be anything.
	 */
	public TreeNode(final Object value) {
		this.value = value;
	}
	
	public boolean equals(final Object object) {
		if (object instanceof TreeNode) {
			return TreeNode.equals(this.value, ((TreeNode) object).value);
		}

		return false;
	}

	/**
	 * Returns the child nodes. Empty arrays are converted to <code>null</code>
	 * before being returned.
	 * 
	 * @return The child nodes; may be <code>null</code>, but never empty.
	 *         There should be no <code>null</code> children in the array.
	 */
	public TreeNode[] getChildren() {
		if (children != null && children.length == 0) {
			return null;
		}
		return children;
	}

	/**
	 * Returns the parent node.
	 * 
	 * @return The parent node; may be <code>null</code> if there are no
	 *         parent nodes.
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Returns the value held by this node.
	 * 
	 * @return The value; may be anything.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns whether the tree has any children.
	 * 
	 * @return <code>true</code> if its array of children is not
	 *         <code>null</code> and is non-empty; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasChildren() {
		return children != null && children.length > 0;
	}
	
	public int hashCode() {
		return TreeNode.hashCode(value);
	}

	/**
	 * Sets the children for this node.
	 * 
	 * @param children
	 *            The child nodes; may be <code>null</code> or empty. There
	 *            should be no <code>null</code> children in the array.
	 */
	public void setChildren(final TreeNode[] children) {
		this.children = children;
	}

	/**
	 * Sets the parent for this node.
	 * 
	 * @param parent
	 *            The parent node; may be <code>null</code>.
	 */
	public void setParent(final TreeNode parent) {
		this.parent = parent;
	}
	
	/**
	 * Provides a hash code for the object -- defending against
	 * <code>null</code>.
	 * 
	 * @param object
	 *            The object for which a hash code is required.
	 * @return <code>object.hashCode</code> or <code>0</code> if
	 *         <code>object</code> if <code>null</code>.
	 */
	public static final int hashCode(final Object object) {
		return object != null ? object.hashCode() : 0;
	}

	/**
	 * Checks whether the two objects are <code>null</code> -- allowing for
	 * <code>null</code>.
	 * 
	 * @param left
	 *            The left object to compare; may be <code>null</code>.
	 * @param right
	 *            The right object to compare; may be <code>null</code>.
	 * @return <code>true</code> if the two objects are equivalent;
	 *         <code>false</code> otherwise.
	 */
	public static final boolean equals(final Object left, final Object right) {
		return left == null ? right == null : ((right != null) && left
				.equals(right));
	}

}
