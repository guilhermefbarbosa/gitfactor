package br.com.guilhermebarbosa.gitfactor;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import gr.uom.java.ast.Standalone;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.junit.Test;

public class JDeodorantTest {
	@Test
	public void test() {
		List<MoveMethodCandidateRefactoring> moveMethods = Standalone.getMoveMethodRefactoringOpportunities(new IJavaProject() {
			
			@Override
			public void save(IProgressMonitor arg0, boolean arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void open(IProgressMonitor arg0) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void makeConsistent(IProgressMonitor arg0) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isOpen() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isConsistent() throws JavaModelException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasUnsavedChanges() throws JavaModelException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public IBuffer getBuffer() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String findRecommendedLineSeparator() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void close() throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object getAdapter(Class arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isStructureKnown() throws JavaModelException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isReadOnly() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public IResource getUnderlyingResource() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ISchedulingRule getSchedulingRule() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IResource getResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement getPrimaryElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPath getPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement getParent() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IOpenable getOpenable() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaProject getJavaProject() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaModel getJavaModel() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getHandleIdentifier() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getElementType() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getElementName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IResource getCorrespondingResource() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAttachedJavadoc(IProgressMonitor arg0)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement getAncestor(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean exists() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasChildren() throws JavaModelException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public IJavaElement[] getChildren() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setRawClasspath(IClasspathEntry[] arg0, IClasspathEntry[] arg1,
					IPath arg2, IProgressMonitor arg3) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRawClasspath(IClasspathEntry[] arg0, IPath arg1,
					boolean arg2, IProgressMonitor arg3) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRawClasspath(IClasspathEntry[] arg0, IPath arg1,
					IProgressMonitor arg2) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRawClasspath(IClasspathEntry[] arg0, boolean arg1,
					IProgressMonitor arg2) throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRawClasspath(IClasspathEntry[] arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOutputLocation(IPath arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOptions(Map arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOption(String arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public IClasspathEntry[] readRawClasspath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPath readOutputLocation() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ITypeHierarchy newTypeHierarchy(IType arg0, IRegion arg1,
					WorkingCopyOwner arg2, IProgressMonitor arg3)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ITypeHierarchy newTypeHierarchy(IType arg0, IRegion arg1,
					IProgressMonitor arg2) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ITypeHierarchy newTypeHierarchy(IRegion arg0, WorkingCopyOwner arg1,
					IProgressMonitor arg2) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ITypeHierarchy newTypeHierarchy(IRegion arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IEvaluationContext newEvaluationContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isOnClasspath(IResource arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isOnClasspath(IJavaElement arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasClasspathCycle(IClasspathEntry[] arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasBuildState() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public IClasspathEntry[] getResolvedClasspath(boolean arg0)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getRequiredProjectNames() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IClasspathEntry[] getReferencedClasspathEntries()
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IClasspathEntry[] getRawClasspath() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IProject getProject() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragment[] getPackageFragments() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot[] getPackageFragmentRoots()
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot getPackageFragmentRoot(IResource arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot getPackageFragmentRoot(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPath getOutputLocation() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map getOptions(boolean arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getOption(String arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object[] getNonJavaResources() throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot[] getAllPackageFragmentRoots()
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, String arg1, WorkingCopyOwner arg2,
					IProgressMonitor arg3) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, String arg1, WorkingCopyOwner arg2)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, String arg1, IProgressMonitor arg2)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, WorkingCopyOwner arg1,
					IProgressMonitor arg2) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, String arg1) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, WorkingCopyOwner arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0, IProgressMonitor arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IType findType(String arg0) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragmentRoot findPackageFragmentRoot(IPath arg0)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IPackageFragment findPackageFragment(IPath arg0)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement findElement(String arg0, WorkingCopyOwner arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement findElement(IPath arg0, WorkingCopyOwner arg1)
					throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IJavaElement findElement(IPath arg0) throws JavaModelException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String encodeClasspathEntry(IClasspathEntry arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IClasspathEntry decodeClasspathEntry(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		System.out.println(moveMethods.toString());
	}
}